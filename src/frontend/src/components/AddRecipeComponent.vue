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

        <div class="form-group">
          <label>Select Measurement:</label>
          <div class="custom-control custom-switch">
            <input type="checkbox" class="custom-control-input" id="measurementSwitch" v-model="isWeightMode">
            <label class="custom-control-label" for="measurementSwitch">Weight</label>
          </div>
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
            <input
                type="text"
                v-model="ingredientItem[isWeightMode ? 'weight' : 'volume']"
                class="form-control"
                :id="'ingredientWeightInput-' + index"
                :placeholder="isWeightMode ? 'Enter weight' : 'Enter volume'"
                @input="validateWeightOrVolume(ingredientItem, index)"
            >

            <div class="input-group-append">
              <!-- Button to remove ingredient -->
              <button type="button" class="btn btn-outline-secondary" @click="removeIngredient(index)" v-if="index > 0">-</button>
              <!-- Button to add ingredient -->
              <button type="button" class="btn btn-outline-secondary" @click="addIngredient()" v-if="index === recipe.recipeIngredients.length - 1">+</button>
            </div>
          </div>
        </div>
        <label>Upload image</label>
        <br>
        <input type="file" ref="uploadImage" @change="onImageUpload" />

        <br><br>
        <button type="submit" class="btn btn-primary" :disabled="!isFormValid">Create recipe</button>
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
            weight: '',
            volume: ''
          },
        ],
        recipeImages: [
          {
            imageName: '',
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
      this.recipe.recipeIngredients.push({ ingredient: { id: null, ingredient: '' }, weight: '', volume: '' });
    },
    removeIngredient(index) {
      // Remove the ingredient and its weight at the specified index
      this.recipe.recipeIngredients.splice(index, 1);
    },
    async saveRecipe() {
      try {
        await this.fileUpload();
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
        });
        const recipeId = response.data;
        await this.$router.push({name: 'recipeModify', params: {recipeId: recipeId}});
      } catch (error) {
        this.errorMessage = error.response.data.message || 'An error occurred while saving the recipe.';
      }
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
              this.recipe.recipeImages[0].imageName = response.data;
              console.log("Recipe IMAGE NAME: " + this.recipe.recipeImages);
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
