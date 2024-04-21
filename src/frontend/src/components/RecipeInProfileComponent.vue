<template>
  <div>
    <div v-if="recipe">
      <div>
        <strong> {{ recipe.title }}</strong>
      </div>
      <img width="150px" :src="recipe.image" />
      <div>
        <strong>Ingredients:</strong>
        <ul>
          <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
            {{ ingredientItem.ingredient.ingredient }}
          </li>
        </ul>
      </div>
    </div>
    <div v-else>
      <p>Loading...</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'RecipeInProfileComponent',
  data() {
    return {
      recipe: null,
    };
  },
  mounted() {
    this.fetchRecipeDetails();
  },
  computed: {
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

</style>
