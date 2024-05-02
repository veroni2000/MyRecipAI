<template>
  <div>
    <div v-if="!startedGenerating">
      <p>Enter Ingredients:</p>
      <textarea id="ingredients" v-model="ingredientInput" rows="4" @input="sanitizeInput"></textarea>
      <br>
      <button @click="generateRecipe">Generate Recipe</button>
    </div>
    <div v-if="finishedGenerating">
      <h2>Generated Recipe: {{ recipe.title }}</h2>
      <div v-if="imageUrl">
        <img :src="imageUrl" alt="Image" width="30%"/>
      </div>
      <h3>Instructions:</h3>
      <p>{{ recipe.instructions }}</p>
      <h3>Ingredients:</h3>
      <ul>
        <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
          {{ ingredientItem.ingredient.ingredient }}: {{ ingredientItem.weight }}, {{ ingredientItem.volume }}
        </li>
      </ul>
      <p v-if="recipe.recipePrice">Estimated time to complete the recipe, calories and price:</p>
      <ul>
        <li v-if="recipe.recipeTime">Time: {{ recipe.recipeTime }}</li>
        <li v-if="recipe.recipeCalories">Calories: {{ recipe.recipeCalories }}</li>
        <li v-if="recipe.recipePrice">Price: €{{ recipe.recipePrice }}</li>
      </ul>
      <br>
      <button @click="saveRecipe">Save Recipe</button>
    </div>
    <div v-else>
      <div v-if="startedGenerating">
        <h3>Generating recipe with {{ ingredientInput }}...</h3>
        <p>{{ loadingText }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "GenerateRecipeComponent",
  data() {
    return {
      ingredientInput: "",
      recipe: {
        id: null,
        title: '',
        instructions: '',
        image: '',
        defaultMetric: 'weight',
        recipeIngredients: [],
        recipeTime: '',
        recipeCalories: '',
        recipePrice: '',
        aiGenerated: true,
      },
      imageUrl: '',
      startedGenerating: false,
      finishedGenerating: false,
      loadingText: '',
    };
  },
  methods: {
    sanitizeInput(event) {
      const inputValue = event.target.value;
      const sanitizedValue = inputValue.replace(/[^a-zA-Z0-9,. ]/g, '');
      // Update the data property with the sanitized value
      this.ingredientInput = sanitizedValue;
    },
    async generateRecipe() {
      this.startedGenerating = true;
      this.loadingText += 'Creating recipe and adding ingredients...\n';
      // Split ingredientInput into separate lines
      const generatedResponse = await axios.get(`/api/recipe/generate?prompt=${encodeURIComponent(this.ingredientInput)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      });
      console.log(generatedResponse.data);

      const parts = generatedResponse.data.split('{separator}');

      const titlePart = parts.find(part => part.includes('Title'));
      const instructionsPart = parts.find(part => part.includes('Instructions'));
      const ingredientsPart = parts.find(part => part.includes('Ingredients'));

      const timePart = parts.find(part => part.includes('Time'));
      const pricePart = parts.find(part => part.includes('Price'));
      const calPart = parts.find(part => part.includes('Calories'));

      if (titlePart) {
        this.recipe.title = titlePart.split('Title:')[1].trim();
        console.log('TITLE ' + this.recipe.title);
      }
      if (instructionsPart) {
        this.recipe.instructions = instructionsPart.split('Instructions:')[1].trim();
        console.log('INSTRUCTIONS ' + this.recipe.instructions);
      }

      if (timePart) {
        this.recipe.recipeTime = timePart.split('Time:')[1].trim();
        console.log('TIME ' + this.recipe.recipeTime);
      }
      if (calPart) {
        this.recipe.recipeCalories = parseInt(calPart.split('Calories:')[1].trim());
        console.log('CALS ' + this.recipe.recipeCalories);
      }
      if (pricePart) {
        this.recipe.recipePrice = parseFloat(pricePart.split('Price:')[1].trim());
        console.log('PRICE ' + this.recipe.recipePrice);
      }

      if (ingredientsPart) {
        const lines = ingredientsPart.split('*').map(line => line.trim());

        for (const line of lines) {
          if (line && line.includes('-')) {
            const [ingredientInfo, weight] = line.split('-').map(item => item.trim());

            const ingredientName = ingredientInfo.trim().toLowerCase();

            console.log(ingredientName + ' - ' + weight);

            await axios.get(`/api/ingredient/find?name=${ingredientName}`, {
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
              }
            })
                .then(async response => {
                  console.log("RESPONSE: "+ JSON.stringify(response.data));
                  if (response.data) {
                    // If ingredient already exists, use its ID
                    const existingIngredientId = response.data.id;

                    console.log(`Using existing ingredient with ID: ${existingIngredientId}`);
                  } else {
                    // If ingredient does not exist, add it
                    await axios.post('/api/ingredient', {ingredient: ingredientName}, {
                      headers: {
                        'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
                      }
                    })
                        .then(newIngredientResponse => {
                          const newIngredientId = newIngredientResponse.data;
                          console.log(`New ingredient ${ingredientName} added with ID: ${newIngredientId}`);
                        })
                        .catch(error => {
                          console.error('Error adding ingredient:', error);
                        });
                  }
                })
                .catch(error => {
                  console.error('Error searching ingredients:', error);
                });

            await axios.get(`/api/ingredient/find?name=${ingredientName}`, {
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
              }
            })
                .then(response => {
                  this.recipe.recipeIngredients.push({
                    id: null,
                    ingredient: response.data,
                    weight: weight,
                  });
                })
                .catch(error => {
                  console.error('Error searching ingredients:', error);
                });


            // this.recipe.recipeIngredients.push({
            //   id: null,
            //   ingredient: {id: ingredientId, ingredient: ingredientName},
            //   weight: weight,
            // });
          }
        }
      }
      this.loadingText += 'Generating image...\n';
      //await this.getImage();
      this.loadingText += 'Converting measurements...\n';
      await this.getVolume();
      this.finishedGenerating = true;
    },
    async getVolume() {
      for (const ingredient of this.recipe.recipeIngredients) {
        console.log(JSON.stringify(ingredient));
        this.promptGramsToCups = `${ingredient.ingredient.ingredient}: ${ingredient.weight}\n`;
        console.log(JSON.stringify(this.promptGramsToCups));
        const volumeResponse = await axios.get(`/api/public/gramsToCups?msg=${this.promptGramsToCups}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          },
        });
        this.promptGramsToCups = "";
        ingredient.volume = volumeResponse.data;
        console.log(ingredient.ingredient.ingredient + ' - ' + ingredient.volume);
      }
    },
    async getImage() {
      console.log("Instructions " + this.recipe.instructions);
      let prompt = '';

      // Concatenate recipe details to the prompt
      prompt += `Title: ${this.recipe.title}\n`;
      prompt += `Instructions: ${this.recipe.instructions}\n`;
      prompt += 'Ingredients:\n';
      this.recipe.recipeIngredients.forEach((ingredient) => {
        prompt += `- ${ingredient.ingredient.ingredient}\n`;
      });
      console.log(JSON.stringify(prompt));

      const imageResponse = await axios.get(`/api/recipe/image?prompt=${encodeURIComponent(prompt)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      });

      this.imageUrl = imageResponse.data;
      console.log(this.imageUrl);
      await this.getImageFromUrl(this.imageUrl);
    },
    async getImageFromUrl() {
      await axios.get(`/api/recipe/saveUrlImage?imageUrl=${encodeURIComponent(this.imageUrl)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      })
          .then(response => {
            console.log('Image added successfully');
            console.log(response.data);
            this.recipe.image = response.data;
          })
          .catch(error => {
            console.error('Error adding image:', error);
          });
    },
    async saveRecipe() {
      //await this.getImageFromUrl(this.imageUrl);
      try {
        console.log(JSON.stringify(this.recipe));
        const response = await axios.post('/api/recipe', {
          title: this.recipe.title,
          instructions: this.recipe.instructions,
          recipeIngredients: this.recipe.recipeIngredients,
          createdBy: localStorage.getItem('id'),
          defaultMetric: this.recipe.defaultMetric,
          recipeTime: this.recipe.recipeTime,
          recipeCalories: this.recipe.recipeCalories,
          recipePrice: this.recipe.recipePrice,
          image: this.recipe.image,
          aiGenerated: this.recipe.aiGenerated,
        }, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          },
        });
        const recipeId = response.data;
        await this.$router.push({name: 'recipe', params: {recipeId: recipeId}});
      } catch (error) {
        this.errorMessage = error.response.data.message || 'An error occurred while saving the recipe.';
      }
    },
  }
};
</script>

<style scoped>

</style>