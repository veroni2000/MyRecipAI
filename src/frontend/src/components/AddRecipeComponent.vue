<template>
  <div class="add-recipe">
    <h2 align="center">Add Recipe</h2>

    <div class="col-sm-6 mx-auto">
      <form @submit.prevent="saveRecipe">
        <div class="form-group">
          <label for="recipeTitleInput">Title</label>
          <br>
          <input type="text" v-model="recipe.title" class="form-control" id="recipeTitleInput"
                 placeholder="Enter recipe title"/>
        </div>

        <div class="form-group">
          <label for="recipeContentInput">Instructions</label>
          <br>
          <textarea v-model="recipe.instructions" class="form-control" id="recipeContentInput" rows="5"
                    placeholder="Enter recipe instructions" @keydown.enter.prevent="handleEnter"/>
        </div>

        <div class="form-group">
          <label>Select Measurement:</label>
          <div class="custom-control custom-switch">
            <div class="switch-container">
              <p style="display: inline-block; margin-right: 10px;">Volume</p>
              <MDBSwitch class="custom-control-input" id="measurementSwitch" v-model="isWeightMode"></MDBSwitch>
              <p style="display: inline-block; margin-left: 10px;">Weight</p>
            </div>
          </div>
        </div>

        <div class="form-group" v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
          <label :for="'ingredientInput-' + index">Ingredient {{ index + 1 }}</label>
          <i class="fas fa-trash-can" id="remove-ingredient" @click="removeIngredient(index)" v-if="index> 0"
             style="margin-left: 5px"></i>
          <div class="input-group">
            <!-- Add ingredient component -->
            <add-ingredient-component
                v-model="ingredientItem.ingredient"
                class="form-control"
                :id="'ingredientInput-' + index"
                @ingredient-selected="(ingredient) => handleIngredientSelected(ingredient, index)"
                style="margin-bottom: 10px"
            ></add-ingredient-component>

            <!-- Input for weight -->
            <input
                type="text"
                v-model="ingredientItem[isWeightMode ? 'weight' : 'volume']"
                class="form-control"
                :id="'ingredientWeightInput-' + index"
                :placeholder="isWeightMode ? 'Enter weight' : 'Enter volume'"
                @input="validateWeightOrVolume(ingredientItem, index)"
            >
          </div>
          <div class="input-group-append">
            <!-- Button to add ingredient -->
            <i class="fas fa-circle-plus fa-2x" id="add-ingredient" @click="addIngredient()"
               v-if="index === recipe.recipeIngredients.length - 1"
               style="margin-top: 5px; margin-bottom: 10px;color: #aec1b5"></i>
            <!--              <button type="button" class="btn btn-outline-secondary" @click="addIngredient()" v-if="index === recipe.recipeIngredients.length - 1">+</button>-->
          </div>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="!isFormValid">Create recipe</button>
      </form>
    </div>
    <div class="recipe-notes">
      <pre><i class="fas fa-circle-info"></i> Note that some improvements will be done to the recipe after creation.
      <i class="fas fa-circle-info"></i> The recipe will appear with converted measurements, added picture, and estimated time, calories, and price.
      <i class="fas fa-circle-info"></i> You will be able to edit your recipes at any time.
      </pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import AddIngredientComponent from "@/components/AddIngredientComponent";
import {
  MDBSwitch,
} from "mdb-vue-ui-kit";

export default {
  name: 'AddRecipeComponent',
  components: {
    AddIngredientComponent,
    MDBSwitch,
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
            weight: '',
            volume: ''
          },
        ],
        image: "",
      },
      errorMessage: null,
      isWeightMode: true // Initially set to weight mode
    };
  },
  computed: {
    isFormValid() {
      return (
          this.recipe.title &&
          this.recipe.instructions &&
          this.recipe.recipeIngredients.every(item => {
            return item.ingredient.ingredient && (this.isWeightMode ? item.weight : item.volume);
          })
      );
    }
  },
  methods: {
    addIngredient() {
      // Add a new empty ingredient object and weight
      this.recipe.recipeIngredients.push({ingredient: {id: null, ingredient: ''}, weight: '', volume: ''});
    },
    removeIngredient(index) {
      // Remove the ingredient and its weight at the specified index
      this.recipe.recipeIngredients.splice(index, 1);
    },
    async saveRecipe() {
      console.log(JSON.stringify(this.recipe));
      const response = await axios.post('/api/recipe', {
        title: this.recipe.title,
        instructions: this.recipe.instructions,
        recipeIngredients: this.recipe.recipeIngredients,
        createdBy: localStorage.getItem('id'),
        defaultMetric: this.isWeightMode ? 'weight' : 'volume',
        recipeImages: this.recipe.recipeImages,
      }, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
      }).catch(error => {
        console.error('Error:', error);
        this.errorMessage = error.response.data.message || 'An error occurred while saving the recipe.';
      });
      const recipeId = response.data;
      await this.$router.push({name: 'recipeModify', params: {recipeId: recipeId}});
    },
    handleIngredientSelected(ingredientName, index) {
      ingredientName = ingredientName.toLowerCase();
      axios.get(`/api/ingredient/find?name=${ingredientName}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            this.recipe.recipeIngredients[index].ingredient = response.data;
            this.showResults = true;
          })
          .catch(error => {
            console.error('Error searching ingredients:', error);
          });
    },
    handleWeightEntered(weight, index) {
      // Add the entered weight to the recipe at the specified index
      this.recipe.recipeIngredients[index].weight = weight;
    },
    validateWeightOrVolume(ingredientItem) {
      // Get the input value based on current mode (weight or volume)
      let value = this.isWeightMode ? ingredientItem.weight : ingredientItem.volume;

      // Regular expression to match valid characters
      const regex = /^[a-zA-Z0-9./\s]*$/;

      // Check if the input value matches the regex pattern
      if (!regex.test(value)) {
        // If the input value does not match, remove the invalid characters
        if (this.isWeightMode) {
          ingredientItem.weight = value.replace(/[^a-zA-Z0-9./\s]/g, '');
        } else {
          ingredientItem.volume = value.replace(/[^a-zA-Z0-9./\s]/g, '');
        }
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
<style>
.switch-container {
  display: inline-flex;
  align-items: center;
}

.add-recipe {
  max-width: 60%;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

#remove-ingredient {
  margin-top: 10px;
  margin-left: 5px;
}

#remove-ingredient:hover, #add-ingredient:hover {
  transform: translateY(-3px);
}

.recipe-notes {
  margin-top: 20px;
  /*text-align: justify;*/
}

pre {
  white-space: pre-wrap;
}
</style>