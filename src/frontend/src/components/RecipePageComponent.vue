<template>
  <div class="recipe-details">
    <h2 class="title">Recipe Details</h2>
    <div v-if="recipe" class="details">
      <div class="detail-item">
        <strong>Title:</strong> {{ recipe.title }}
      </div>
      <div class="detail-item">
        <strong>Instructions:</strong> {{ recipe.instructions }}
      </div>
      <div class="detail-item">
        <strong>Created By: </strong>
        <router-link :to="profileLink" class="created-by-link">{{ recipe.createdBy.firstName + ' ' + recipe.createdBy.lastName}}</router-link>
      </div>
      <div class="detail-item">
        <strong>Ingredients:</strong>
        <ul class="ingredient-list">
          <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient">
            {{ ingredientItem.ingredient.ingredient }} - {{ ingredientItem.weight }}
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

export default {
  name: 'RecipePageComponent',
  data() {
    return {
      recipe: null,
      createdBy: {
        id: null,
        firstName: null,
        lastName: null,
      }
    };
  },
  mounted() {
    this.fetchRecipeDetails();
  },
  computed: {
    profileLink() {
      const userId = this.recipe.createdBy.id;
      return { name: 'user', params: { userId } };
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
          })
          .catch(error => {
            console.error('Error fetching recipe details:', error);
          });
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
