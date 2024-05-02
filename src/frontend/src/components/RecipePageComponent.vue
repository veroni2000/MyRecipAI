<template>
  <div class="recipe-details">
    <div v-if="recipe" class="details">
      <div class="detail-item">
        <h2 class="title">{{ recipe.title }} <MDBBadge v-if="recipe.aiGenerated" color="primary">AI</MDBBadge></h2>
      </div>
      <div v-if="isCurrentUser" class="edit-profile-link">
        <router-link :to="'/recipe/edit/' + recipe.id">Edit recipe</router-link>
      </div>
      <br>
      <div class="detail-item">
        <strong>Created By: </strong>
        <router-link :to="profileLink" class="created-by-link">
          {{ recipe.createdBy.firstName + ' ' + recipe.createdBy.lastName }}
        </router-link>
      </div>
      <img v-if="recipe.image" :src="require(`../../../main/resources/images/${recipe.image}`)" :alt="recipe.image" width="60%" />
      <div v-if="recipe.recipeTime" class="detail-item">
        <strong>Time: </strong> {{ recipe.recipeTime }}
      </div>
      <div v-if="recipe.recipeCalories" class="detail-item">
        <strong>Calories: </strong> {{ recipe.recipeCalories }}
      </div>
      <div v-if="recipe.recipePrice" class="detail-item">
        <strong>Price: </strong>€{{ recipe.recipePrice }}
      </div>
      <div class="detail-item">
        <strong>Instructions:</strong>
        <p>{{ recipe.instructions }}</p>
      </div>
      <div class="detail-item">
        <strong>Ingredients:</strong>
        <div class="toggle-switch">
          <label>
            Show in:
            <select v-model="displayMode">
              <option value="weight">Weight</option>
              <option value="volume">Volume</option>
            </select>
          </label>
        </div>
        <ul class="ingredient-list">
          <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient">
            {{ ingredientItem.ingredient.ingredient }} - {{ displayIngredientValue(ingredientItem) }}
          </li>
        </ul>
      </div>
    </div>
    <div v-else class="loading">
      <p>Loading...</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { MDBBadge } from "mdb-vue-ui-kit";

export default {
  name: 'RecipePageComponent',
  components: {
    MDBBadge
  },
  data() {
    return {
      recipe: null,
      createdBy: {
        id: null,
        firstName: null,
        lastName: null,
      },
      displayMode: 'weight' // Default display mode
    };
  },
  mounted() {
    this.fetchRecipeDetails();
  },
  computed: {
    profileLink() {
      const userId = this.recipe.createdBy.id;
      return {name: 'user', params: {userId}};
    },
    recipeImageUrl() {
      // Use ImageService to get the image URL based on recipe.image
      let name = '../../../main/resources/images/'
      console.log(this.recipe.image);
      return this.recipe.image ? name + this.recipe.image : null;
    },
  },
  methods: {
    async isCurrentUser() {
      await this.fetchRecipeDetails(); // Ensure recipe data is fetched

      if (this.recipe && this.recipe.createdBy) {
        const userIdFromRecipe = this.recipe.createdBy.id;
        const loggedInUserId = localStorage.getItem("id");
        return userIdFromRecipe === loggedInUserId;
      } else {
        return false;
      }
    },
    async fetchRecipeDetails() {
      const recipeId = this.$route.params.recipeId;

      axios.get(`/api/recipe/${recipeId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            this.recipe = response.data;
            // Set default display mode based on recipe defaultMetric
            this.displayMode = this.recipe.defaultMetric === 'volume' ? 'volume' : 'weight';
          })
          .catch(error => {
            console.error('Error fetching recipe details:', error);
          });
    },
    displayIngredientValue(ingredientItem) {
      return this.displayMode === 'weight' ? ingredientItem.weight : ingredientItem.volume;
    }
  }
};
</script>

<style scoped>
.recipe-details {
  max-width: 800px;
  margin: 0 auto;
}

.title {
  margin-bottom: 20px;
}

.detail-item {
  margin-bottom: 10px;
}

.created-by-link {
  text-decoration: none;
  color: #007bff;
}

.ingredient-list {
  list-style-type: none;
  padding-left: 0;
}

.ingredient {
  margin-bottom: 5px;
}
</style>
