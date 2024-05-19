<template>
  <div class="edit-recipe-container">
    <h2 align="center">Edit Recipe</h2>
    <div class="form-wrapper">
      <form @submit.prevent="updateRecipe" class="edit-recipe-form">
        <!-- Title Input -->
        <div class="form-group">
          <label for="recipeTitleInput">Title</label>
          <input type="text" v-model="recipe.title" class="form-control" id="recipeTitleInput" placeholder="Enter recipe title">
        </div>

        <!-- Instructions Textarea -->
        <div class="form-group">
          <label for="recipeContentInput">Instructions</label>
          <textarea v-model="recipe.instructions" class="form-control" id="recipeContentInput" rows="5" placeholder="Enter recipe instructions" @keydown.enter.prevent="handleEnter"></textarea>
        </div>

        <!-- Ingredients -->
        <div class="form-group" v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
          <!-- Ingredient Display -->
          <div class="ingredient-display">
            <span class="ingredient-name">{{ ingredientItem.ingredient.ingredient }}</span>
          </div>
          <!-- Ingredient Measurements -->
          <div class="ingredient-measurements">
            <label>Weight</label>
            <input type="text" v-model="ingredientItem.weight" class="form-control" :id="'ingredientWeightInput-' + index" placeholder="Enter weight">
            <label>Volume</label>
            <input type="text" v-model="ingredientItem.volume" class="form-control" :id="'ingredientVolumeInput-' + index" placeholder="Enter volume">
          </div>
        </div>

        <!-- Time -->
        <div class="form-group">
          <label for="recipeTimeInput">Time</label>
          <input type="text" v-model="recipe.recipeTime" class="form-control" id="recipeTimeInput" placeholder="Enter time">
        </div>

        <!-- Calories -->
        <div class="form-group">
          <label for="recipeCaloriesInput">Calories</label>
          <input type="text" v-model="recipe.recipeCalories" class="form-control" id="recipeCaloriesInput" placeholder="Enter calories">
        </div>

        <!-- Price -->
        <div class="form-group">
          <label for="recipePriceInput">Price</label>
          <input type="text" v-model="recipe.recipePrice" class="form-control" id="recipePriceInput" placeholder="Enter price">
        </div>

        <div class="form-group">
          <label>Upload image:</label>
          <input type="file" accept="image/*" ref="uploadImage" @change="onImageUpload"/>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="!isFormValid">Save changes</button>
          <router-link :to="'/recipe/' + recipe.id" class="btn btn-secondary">Cancel</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'EditRecipeComponent',
  data() {
    return {
      recipe: {
        id: null,
        title: '',
        instructions: '',
        recipeIngredients: [
          {
            id: null,
            ingredient: { id: null, ingredient: '' },
            weight: '',
            volume: ''
          }
        ],
        recipeTime: '',
        recipeCalories: '',
        recipePrice: '',
        image: '',
      },
    };
  },
  mounted() {
    this.fetchRecipeDetails();
  },
  computed: {
    isFormValid() {
      return (
          this.recipe.title &&
          this.recipe.instructions &&
          this.recipe.recipeIngredients.every(item => {
            return item.ingredient.ingredient && item.weight && item.volume;
          })
      );
    }
  },
  methods: {
    fetchRecipeDetails() {
      const recipeId = this.$route.params.recipeId;

      axios.get(`/api/recipe/${recipeId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            this.recipe = response.data;
            // Check if the current user is the owner of the recipe
            if(localStorage.getItem('id') != this.recipe.createdBy.id){
              this.$router.push({ name: 'recipe', params: { recipeId: this.recipe.id } });
            }
          })
          .catch(error => {
            console.error('Error fetching recipe details:', error);
          });
    },
    async updateRecipe() {
      await this.fileUpload();
      axios.post(`/api/recipe/edit/${this.recipe.id}`, this.recipe, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            console.log('Recipe updated successfully:', response.data);
            this.$router.push({name: 'recipe', params: {recipeId: this.recipe.id}});
          })
          .catch(error => {
            console.error('Error updating recipe:', error);
          });
    },
    validateWeightOrVolume(ingredientItem) {
      let valueWeight = ingredientItem.weight;
      let valueVolume = ingredientItem.volume;

      // Regular expression to match valid characters
      const regex = /^[a-zA-Z0-9./\s]*$/;

      // Check if the input value matches the regex pattern
      if (!(regex.test(valueWeight))||!(regex.test(valueVolume))) {
        // If the input value does not match, remove the invalid characters
          ingredientItem.weight = valueWeight.replace(/[^a-zA-Z0-9./\s]/g, '');
          ingredientItem.volume = valueVolume.replace(/[^a-zA-Z0-9./\s]/g, '');
      }
    },
    handleEnter(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        // Insert a newline character at the cursor position
        const cursorPosition = event.target.selectionStart;
        this.recipe.instructions = this.recipe.instructions.slice(0, cursorPosition) + '\n' + this.recipe.instructions.slice(cursorPosition);
      }
    },
    onImageUpload() {
      let file = this.$refs.uploadImage.files[0];
      this.formData = new FormData();
      this.formData.append("file", file);
      //this.recipe.recipeImages.push({ imageName: '' });
    },
    async fileUpload() {
      if (this.formData != null) {
        await axios.post(`/api/recipe/saveImage`, this.formData, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
            'Content-Type': 'multipart/form-data' // Set Content-Type to multipart/form-data
          }
        })
            .then(response => {
              console.log('Image saved successfully:', response.data);
              this.recipe.image = response.data;
              console.log("Recipe IMAGE NAME: " + this.recipe.image);
              // Handle response data as needed
            })
            .catch(error => {
              console.error('Error saving image:', error);
              // Handle error if the request fails
            });
      }
    }
  }
};
</script>

<style scoped>
.edit-recipe-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.form-wrapper {
  width: 100%;
}

.edit-recipe-form {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-group {
  margin-bottom: 20px;
  width: 100%;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1em;
}

.ingredient-display {
  margin-bottom: 5px;
  font-weight: bold;
}

.ingredient-measurements {
  display: flex;
  gap: 10px;
  align-items: center;
}

.ingredient-measurements label {
  font-weight: normal;
}

.ingredient-measurements input {
  width: 40%;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  text-decoration: none;
  text-align: center;
}

.btn-secondary:hover {
  background-color: #5a6268;
}
</style>
