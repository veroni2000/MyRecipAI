<template>
  <div class="recipe-page">
    <h2 v-if="recipe.recipePrice === 'none'">
      Note that some calculations are missing due to inappropriate input. Please consider editing the recipe.
    </h2>
    <h2 v-else-if="recipe.recipePrice">Recipe successfully updated!</h2>
    <p v-if="errorMessage">{{ errorMessage }}</p>
    <div class="recipe-details">
      <div v-if="recipe.image">
        <p>Generated image:</p>
        <img :src="imageUrl" alt="Image" />
      </div>
      <div v-if="recipe.recipePrice">
        <p>
          Calculated measurements for ingredients, so the recipe will be available in both weight and volume:
        </p>
        <ul>
          <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
            {{ ingredientItem.ingredient.ingredient }}: {{ ingredientItem.weight }}, {{ ingredientItem.volume }}
          </li>
        </ul>
        <p>Calculated estimated time to complete the recipe, calories, and estimated price in Euro:</p>
        <ul>
          <li v-if="recipe.recipeTime">Estimated Time: {{ recipe.recipeTime }}</li>
          <li v-if="recipe.recipeCalories">Estimated Calories: {{ recipe.recipeCalories }}</li>
          <li v-if="recipe.recipePrice">Estimated Price: €{{ recipe.recipePrice }}</li>
        </ul>
        <router-link :to="'/recipe/' + recipe.id" class="go-to-recipe-btn">Go to recipe</router-link>
      </div>
      <div v-else-if="finished">
        <p>There was a problem with some calculations. The modification will be finished automatically later.</p>
        <router-link :to="'/recipe/' + recipe.id" class="go-to-recipe-btn">Go to recipe</router-link>
      </div>
      <div v-else class="loading">
        <pre>Loading...
        Making some improvements to the recipe...
        <i class="fas fa-cookie-bite fa-7x fa-spin" style="color: #e6b18e; margin-top: 5rem"></i>
        </pre>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import ImageService from '@/services/ImageService';

export default {
  name: "recipeModifyComponent",
  data() {
    return {
      recipe: {
        id: null,
        title: '',
        instructions: '',
        image: '',
        defaultMetric: '',
        displayImage: '',
        recipeIngredients: [
          {
            id: null,
            ingredient: {id: null, ingredient: ''},
            weight: '',
            volume: ''
          }
        ],
        recipeImages: [
          {
            imageName: '',
          }
        ],
        recipeTime: '',
        recipeCalories: '',
        recipePrice: '',
      },
      imageUrl: '',
      prompt: "",
      promptGramsToCups: "",
      promptCupsToGrams: "",
      displayError: false,
      finished: false,
      errorMessage: '',
    };
  },
  mounted() {
    this.fetchRecipeDetails();
  },
  methods: {
    async fetchRecipeDetails() {
      const recipeId = this.$route.params.recipeId;

      axios.get(`/api/recipe/${recipeId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(async response => {
            this.recipe = response.data;
            if (localStorage.getItem('id') != this.recipe.createdBy.id) {
              await this.$router.push({name: 'recipe', params: {recipeId: this.recipe.id}});
            }

            if (this.recipe.image === null) {
              await this.getImage();
              console.log("Recipe image url: " + this.imageUrl);
              localStorage.setItem('useUrl', '1');
              localStorage.setItem('imageUrl', this.imageUrl);

              await this.getImageFromUrl();

              console.log(this.recipe.image);
              await this.updateRecipe();
            } else await this.loadImage(this.recipe.image);
          })
          .catch(error => {
            console.error('Error fetching recipe details:', error);
          });
    },
    async getImage() {
      console.log("Instructions " + this.recipe.instructions);

      // Concatenate recipe details to the prompt
      this.prompt += `Title: ${this.recipe.title}\n`;
      // this.prompt += `Instructions: ${this.recipe.instructions}\n`;
      this.prompt += 'Ingredients:\n';
      this.recipe.recipeIngredients.forEach((ingredient) => {
        this.prompt += `- ${ingredient.ingredient.ingredient}\n`;
      });
      console.log(JSON.stringify(this.prompt));

      const imageResponse = await axios.get(`/api/recipe/image?prompt=${encodeURIComponent(this.prompt)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      }).catch(error => {
        console.error('Error:', error);
      });

      // Update recipe.image with the generated image
      this.imageUrl = imageResponse.data;
      console.log(this.imageUrl);
    },
    async getImageFromUrl() {
      if (this.imageUrl) {
        await axios.get(`/api/recipe/saveUrlImage?imageUrl=${encodeURIComponent(this.imageUrl)}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          },
        })
            .then(async response => {
              this.recipe.image = response.data;
            })
            .catch(error => {
              this.recipe.image = '';
              console.error('Error adding image:', error);
            });
      }
    },
    async loadImage(imageName) {
      this.imageUrl = await ImageService.getImageUrl(imageName);
    },
    async getMeasurements() {
      console.log(this.recipe.defaultMetric);
      if (this.recipe.defaultMetric == 'weight') {
        for (const ingredient of this.recipe.recipeIngredients) {
          this.promptGramsToCups = `${ingredient.ingredient.ingredient}: ${ingredient.weight}\n`;
          const volumeResponse = await axios.get(`/api/public/gramsToCups?msg=${this.promptGramsToCups}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            },
          });
          this.promptGramsToCups = "";
          if (!volumeResponse.data.toLowerCase().includes('sorry') || !volumeResponse.data.toLowerCase().includes('appropriate')
              || !volumeResponse.data.toLowerCase().includes('cannot') || !volumeResponse.data.toLowerCase().includes('assist')
              || !volumeResponse.data.toLowerCase().includes('n/a')) {
            ingredient.volume = volumeResponse.data;
            this.displayError = true;
            console.log(ingredient.volume);
          }
        }
      } else {
        for (const ingredient of this.recipe.recipeIngredients) {
          this.promptCupsToGrams = `${ingredient.ingredient.ingredient}: ${ingredient.volume}\n`;
          const weightResponse = await axios.get(`/api/public/cupsToGrams?msg=${this.promptCupsToGrams}`, {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            },
          });
          this.promptCupsToGrams = "";
          if (!weightResponse.data.toLowerCase().includes('sorry') || !weightResponse.data.toLowerCase().includes('appropriate')
              || !weightResponse.data.toLowerCase().includes('cannot') || !weightResponse.data.toLowerCase().includes('assist')
              || !weightResponse.data.toLowerCase().includes('n/a')) {
            ingredient.weight = weightResponse.data;
            this.displayError = true;
            console.log(ingredient.weight);
          }
        }
      }
    },
    async getTimeCalPrice() {
      console.log(JSON.stringify(this.prompt));

      try {
        const timeCalPriceResponse = await axios.get(`/api/recipe/timeCalPrice?prompt=${encodeURIComponent(this.prompt)}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          },
        }).catch(error => {
          console.error('Error:', error);
        });

        if (timeCalPriceResponse.data.toLowerCase().includes('sorry') || timeCalPriceResponse.data.toLowerCase().includes('appropriate')
            || timeCalPriceResponse.data.toLowerCase().includes('cannot') || timeCalPriceResponse.data.toLowerCase().includes('assist')
            || timeCalPriceResponse.data.toLowerCase().includes('n/a')) {
          this.displayError = true;
          this.recipe.recipeTime = 'none';
          this.recipe.recipePrice = 'none';
          this.recipe.recipeCalories = 'none';
        }

        console.log(timeCalPriceResponse.data);

        const parts = timeCalPriceResponse.data.split(';');

        const timePart = parts.find(part => part.includes('Time'));
        const pricePart = parts.find(part => part.includes('Price'));
        const calPart = parts.find(part => part.includes('Calories'));

        if (timePart) {
          // Extract the time portion after 'Time'
          this.recipe.recipeTime = timePart.split('Time:')[1].trim();
          console.log(this.recipe.recipeTime);
        }
        if (pricePart) {
          this.recipe.recipePrice = parseFloat(pricePart.split('Price:')[1].trim());
          console.log(this.recipe.recipePrice);
        }
        if (calPart) {
          this.recipe.recipeCalories = parseInt(calPart.split('Calories:')[1].trim());
          console.log(this.recipe.recipeCalories);
        }
      } catch (error) {
        this.recipe.recipeTime = '';
        this.recipe.recipeCalories = '';
        this.recipe.recipePrice = '';
      }
    },
    async updateRecipe() {
      await this.getMeasurements();
      await this.getTimeCalPrice();

      axios.post(`/api/recipe/edit/${this.recipe.id}`, this.recipe, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            console.log('Recipe updated successfully:', response.data);
            //this.$router.push({ name: 'recipe', params: { recipeId: this.recipe.id } });
          })
          .catch(error => {
            console.error('Error updating recipe:', error);
          });
      this.finished = true;
    },
  }
}
</script>

<style scoped>
.recipe-page {
  text-align: center;
  padding: 20px;
}

.recipe-details {
  margin: 20px auto;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  max-width: 600px;
}

.recipe-details img {
  max-width: 100%;
  border-radius: 10px;
  margin-bottom: 20px;
}

.recipe-details p {
  font-size: 1.2em;
  margin-bottom: 20px;
}

.recipe-details ul {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

.recipe-details li {
  background: #f7f7f7;
  padding: 10px;
  margin-bottom: 5px;
  border-radius: 5px;
}

.go-to-recipe-btn {
  display: inline-block;
  padding: 10px 20px;
  background: #007bff;
  color: #fff;
  border-radius: 5px;
  text-decoration: none;
  transition: background 0.3s;
}

.go-to-recipe-btn:hover {
  background: #0056b3;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

pre {
  white-space: pre-wrap;
}
</style>
