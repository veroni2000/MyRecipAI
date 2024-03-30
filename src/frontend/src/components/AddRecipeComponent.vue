<template>
  <div>
    <h2 align="center">Add Recipe</h2>

    <div class="col-sm-6">
      <form @submit.prevent="saveRecipe">
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
          <label :for="'ingredientInput-' + index">Ingredient {{ index + 1 }}</label>
          <div class="input-group">
            <!-- Add ingredient component -->
            <add-ingredient-component
                v-model="ingredientItem.ingredient"
                class="form-control"
                :id="'ingredientInput-' + index"
                @ingredient-selected="(ingredient) => handleIngredientSelected(ingredient, index)"
            ></add-ingredient-component>

            <!-- Input for weight -->
            <input type="text" v-model="ingredientItem.weight" class="form-control" :id="'ingredientWeightInput-' + index" placeholder="Enter weight" @change="handleWeightEntered(ingredientItem.weight, index)">

            <div class="input-group-append">
              <!-- Button to remove ingredient -->
              <button type="button" class="btn btn-outline-secondary" @click="removeIngredient(index)" v-if="index > 0">-</button>
              <!-- Button to add ingredient -->
              <button type="button" class="btn btn-outline-secondary" @click="addIngredient()" v-if="index === recipe.recipeIngredients.length - 1">+</button>
            </div>
          </div>
        </div>

        <br>
        <button type="submit" class="btn btn-primary">Create recipe</button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import AddIngredientComponent from "@/components/AddIngredientComponent";
/* eslint-disable */

export default {
  name: 'AddRecipeComponent',
  components: {
    AddIngredientComponent
  },
  data() {
    return {
      recipe: {
        title: '',
        createdBy: '',
        instructions: '',
        recipeIngredients: [
          {
            ingredient: {
              id: null,
              ingredient: ''
            },
            weight: ''
          },
        ]
      },
      errorMessage: null
    };
  },
  methods: {
    addIngredient() {
      // Add a new empty ingredient object and weight
      this.recipe.recipeIngredients.push({ ingredient: { id: null, ingredient: '' }, weight: '' });
    },
    removeIngredient(index) {
      // Remove the ingredient and its weight at the specified index
      this.recipe.recipeIngredients.splice(index, 1);
    },
    async saveRecipe() {
      console.log(this.recipe);
      try {
        const response = await axios.post('/api/recipe', {
          title: this.recipe.title,
          instructions: this.recipe.instructions,
          recipeIngredients: this.recipe.recipeIngredients,
          createdBy: localStorage.getItem('id')
        }, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          },
        });
        console.log('Response:', response);
        const recipeId = response.data;
        await this.$router.push({ name: 'recipe', params: { recipeId } });
      } catch (error) {
        this.errorMessage = error.response.data.message || 'An error occurred while saving the recipe.';
      }
    },
    handleIngredientSelected(ingredientName, index) {
      axios.get(`/api/ingredient/find?name=${ingredientName}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            this.recipe.recipeIngredients[index].ingredient = response.data;
            this.showResults = true;
            console.log(this.recipe.recipeIngredients[index].ingredient);
            
            console.log(this.recipe.recipeIngredients);
          })
          .catch(error => {
            console.error('Error searching ingredients:', error);
          });
    },
    handleWeightEntered(weight, index) {
      // Add the entered weight to the recipe at the specified index
      this.recipe.recipeIngredients[index].weight = weight;
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
