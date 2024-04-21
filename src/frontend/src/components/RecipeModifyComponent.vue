<template>

  <div v-if="recipe.image">
    <h2>Recipe successfully updated!</h2>
    <p>Generated image:</p>
    <div class="recipe-details">
      <div v-if="recipe.image">
        <img :src="imageUrl" alt="Image"/>
      </div>
      <p>Calculated measurements for ingredients, so the recipe will be available in both weigh and volume:</p>
      <ul>
        <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
          {{ ingredientItem.ingredient.ingredient }}: {{ ingredientItem.weight }}, {{ ingredientItem.volume }}
        </li>
      </ul>
      <p>Calculated estimated time to complete the recipe, calories and estimated price in Euro:</p>
      <ul>
        <li v-if="recipe.recipeTime">Estimated Time: {{ recipe.recipeTime }}</li>
        <li v-if="recipe.recipeCalories">Estimated Calories: {{ recipe.recipeCalories }}</li>
        <li v-if="recipe.recipePrice">Estimated Price: €{{ recipe.recipePrice }}</li>
      </ul>
    </div>
  </div>
  <div v-else class="loading">
    <p>Loading...</p>
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
      this.prompt += `Instructions: ${this.recipe.instructions}\n`;
      this.prompt += 'Ingredients:\n';
      this.recipe.recipeIngredients.forEach((ingredient) => {
        this.prompt += `- ${ingredient.ingredient.ingredient}\n`;
      });
      console.log(JSON.stringify(this.prompt));

      const imageResponse = await axios.get(`/api/recipe/image?prompt=${encodeURIComponent(this.prompt)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      });

      // Update recipe.image with the generated image
      this.imageUrl = imageResponse.data;
      console.log(this.imageUrl);
    },
    async getImageFromUrl() {
      await axios.get(`/api/recipe/saveUrlImage?imageUrl=${encodeURIComponent(this.imageUrl)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      })
          .then(async response => {
            console.log('Image added successfully');
            console.log(response.data);
            this.recipe.image = response.data;
          })
          .catch(error => {
            console.error('Error adding image:', error);
          });
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
          ingredient.volume = volumeResponse.data;
          console.log(ingredient.volume);
        }
        if (this.recipe.defaultMetric == 'volume') {
          for (const ingredient of this.recipe.recipeIngredients) {
            this.promptCupsToGrams = `${ingredient.ingredient.ingredient}: ${ingredient.volume}\n`;
            const weightResponse = await axios.get(`/api/public/cupsToGrams?msg=${this.promptCupsToGrams}`, {
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
              },
            });
            this.promptCupsToGrams = "";
            ingredient.weight = weightResponse.data;
            console.log(ingredient.weight);
          }
        }
      }
    },
    async getTimeCalPrice() {
      console.log(JSON.stringify(this.prompt));

      const timeCalPriceResponse = await axios.get(`/api/recipe/timeCalPrice?prompt=${encodeURIComponent(this.prompt)}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      });

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
    },
    async updateRecipe() {
      if (this.recipe.image === null) {
        //await this.getImage();
        // window.location.href='';
      }
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
    },
  }
}
</script>

<style scoped>

</style>