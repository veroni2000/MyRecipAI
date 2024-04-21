<template>
  <div>
    <h2 align="center">Edit Recipe</h2>

    <div class="col-sm-6">
      <form @submit.prevent="updateRecipe">
        <div class="form-group">
          <label for="recipeTitleInput">Title</label>
          <br>
          <input type="text" v-model="recipe.title" class="form-control" id="recipeTitleInput" placeholder="Enter recipe title">
        </div>

        <div class="form-group">
          <label for="recipeContentInput">Instructions</label>
          <br>
          <textarea v-model="recipe.instructions" class="form-control" id="recipeContentInput" rows="5" placeholder="Enter recipe instructions" @keydown.enter.prevent="handleEnter"></textarea>
        </div>

        <div class="form-group" v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
          <div class="input-group">
            <!-- Display ingredient name (non-editable) -->
            <div class="form-control">{{ ingredientItem.ingredient.ingredient }}</div>

            <!-- Inputs for weight and volume -->
            <label>Weight </label>
            <input
                type="text"
                v-model="ingredientItem['weight']"
                class="form-control"
                :id="'ingredientWeightInput-' + index"
                :placeholder="'Enter weight'"
                @input="validateWeightOrVolume(ingredientItem, index)"
            >
            <br>
            <label>Volume </label>
            <input
                type="text"
                v-model="ingredientItem['volume']"
                class="form-control"
                :id="'ingredientWeightInput-' + index"
                :placeholder="'Enter volume'"
                @input="validateWeightOrVolume(ingredientItem, index)"
            >
          </div>
        </div>

        <br>
        <div class="form-group">
          <button type="submit" class="btn btn-primary" :disabled="!isFormValid">Save changes</button>
          <br>
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
        ]
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
    updateRecipe() {
      axios.post(`/api/recipe/edit/${this.recipe.id}`, this.recipe, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            console.log('Recipe updated successfully:', response.data);
            this.$router.push({ name: 'recipe', params: { recipeId: this.recipe.id } });
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
  }
};
</script>

<style scoped>
/* Add scoped styles if needed */
</style>
