<template>
  <div class="generate-recipe-container">
    <div v-if="!startedGenerating" class="input-section">
      <h2>Enter Ingredients:</h2>
      <textarea id="ingredients" v-model="ingredientInput" rows="4" @input="sanitizeInput" class="textarea-input"></textarea>
      <button type="submit" @click="generateRecipe" :disabled="!ingredientInput" class="btn btn-primary" id="generate-button">Generate Recipe</button>
    </div>
    <div v-if="displayError" class="error-message">
      <p>Something went wrong. Please check your input and make sure it contains food ingredients only.</p>
    </div>
    <div v-else-if="finishedGenerating" class="recipe-section">
      <h2>Generated Recipe: {{ recipe.title }}</h2>
      <div v-if="imageUrl" class="image-container">
        <img :src="imageUrl" alt="Image" class="recipe-image"/>
      </div>
      <h3>Instructions:</h3>
      <p>{{ recipe.instructions }}</p>
      <h3>Ingredients:</h3>
      <ul class="ingredient-list">
        <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient-item">
          {{ ingredientItem.ingredient.ingredient }}: {{ ingredientItem.weight }}, {{ ingredientItem.volume }}
        </li>
      </ul>
      <div v-if="recipe.recipePrice" class="additional-info">
        <p>Estimated time to complete the recipe, calories and price:</p>
        <ul>
          <li v-if="recipe.recipeTime">Time: {{ recipe.recipeTime }}</li>
          <li v-if="recipe.recipeCalories">Calories: {{ recipe.recipeCalories }}</li>
          <li v-if="recipe.recipePrice">Price: €{{ recipe.recipePrice }}</li>
        </ul>
      </div>
      <button @click="saveRecipe" class="save-button">Save Recipe</button>
    </div>
    <div v-else>
      <div v-if="startedGenerating" class="loading-section">
        <h3>Generating recipe with {{ ingredientInput }}...</h3>
        <p>{{ loadingText }}</p>
        <i class="fas fa-cookie fa-7x fa-spin" style="color: #e6b18e; margin-top: 5rem"></i>
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
      displayError: false,
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
      this.displayError = false;
      this.startedGenerating = true;
      this.loadingText += 'Creating recipe and adding ingredients...\n';
      // Split ingredientInput into separate lines
      const generatedResponse = await axios.get(`/api/recipe/generate?prompt=${encodeURIComponent(this.ingredientInput)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      }).catch(error => {
        console.error('Error:', error);
      });
      console.log(generatedResponse.data);
      if (generatedResponse.data.toLowerCase().includes('sorry') || generatedResponse.data.toLowerCase().includes('appropriate')) {
        this.displayError = true;
      }

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
                  console.log("RESPONSE: " + JSON.stringify(response.data));
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

          }
        }
      }
      this.loadingText += 'Generating image...\n';
      await this.getImage();
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
        }).catch(error => {
          console.error('Error:', error);
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
        }).catch(error => {
          console.error('Error:', error);

        });

        this.imageUrl = imageResponse.data;
        console.log(this.imageUrl);
        await this.getImageFromUrl(this.imageUrl);
    },
    async getImageFromUrl() {
      if (this.imageUrl) {
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
      }
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
        }).catch(error => {
          console.error('Error:', error);
        });

        const recipeId = response.data;
        localStorage.setItem('generated', '1');
        localStorage.setItem('useUrl', '1');
        localStorage.setItem('imageUrl', this.imageUrl);
        await this.$router.push({name: 'recipe', params: {recipeId: recipeId}});
      } catch (error) {
        this.errorMessage = error.response.data.message || 'An error occurred while saving the recipe.';
      }
    },
  }
};
</script>

<style scoped>
.generate-recipe-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.input-section {
  text-align: center;
  width: 100%;
}

.input-section h2 {
  margin-bottom: 10px;
  font-size: 1.5em;
  color: #333;
}

.textarea-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1em;
  margin-bottom: 10px;
}

#generate-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

#generate-button:hover {
  background-color: #0056b3;
}

.error-message {
  color: red;
  font-size: 1.1em;
  margin-top: 20px;
}

.recipe-section {
  width: 100%;
  text-align: center;
}

.recipe-section h2 {
  font-size: 2em;
  color: #333;
  margin-bottom: 20px;
}

.image-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.recipe-image {
  max-width: 30%;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.ingredient-list {
  list-style: none;
  padding: 0;
}

.ingredient-item {
  margin-bottom: 10px;
  font-size: 1.1em;
}

.additional-info {
  margin-top: 20px;
  font-size: 1.1em;
}

.save-button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.save-button:hover {
  background-color: #218838;
}

.loading-section {
  text-align: center;
  margin-top: 50px;
}

.loading-section h3 {
  font-size: 1.5em;
  color: #333;
}

.loading-section p {
  font-size: 1.2em;
  color: #666;
}
</style>