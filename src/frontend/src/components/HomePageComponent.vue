<template>
  <div class="home-page">
    <h1 class="title">Home Page</h1>
    <p class="message">{{ msg }}</p>
    <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card">
      <router-link :to="{ name: 'recipe', params: { recipeId: recipe.id }}">
        <div class="recipe-image" :style="{
          backgroundImage: recipe.image ? `url(${require('../../../main/resources/images/' + recipe.image)})` : `url(${require('../../../main/resources/images/defaultRecipe.png')})`
        }">
          <div class="recipe-title">
            <h3>{{ recipe.title }}<MDBBadge v-if="recipe.aiGenerated" color="primary">AI</MDBBadge></h3>
          </div>
        </div>
      </router-link>
      <div class="recipe-details">
<!--        <strong>Ingredients:</strong>-->
        <button @click="toggleIngredients(recipe.id)" class="toggle-ingredients-btn">
          {{ expandedRecipes.includes(recipe.id) ? 'Hide Ingredients' : 'Show Ingredients' }}
        </button>
        <ul v-if="expandedRecipes.includes(recipe.id)">
          <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
            {{ ingredientItem.ingredient.ingredient }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/components/apiClient";
import { MDBBadge } from "mdb-vue-ui-kit";

export default {
  name: "HomePageComponent",
  components: {
    MDBBadge,
  },
  data() {
    return {
      msg: '',
      recipes: [],
      expandedRecipes: [] // Track expanded recipes by their IDs
    };
  },
  mounted() {
    this.msg = localStorage.getItem('jwtToken');
    this.getRecipes();
  },
  methods: {
    getRecipes() {
      axiosInstance.get(`/public/recipes/all`)
          .then(response => {
            this.recipes = response.data;
          })
          .catch(error => {
            console.error('Error fetching recipes:', error);
          });
    },
    toggleIngredients(recipeId) {
      const index = this.expandedRecipes.indexOf(recipeId);
      if (index > -1) {
        this.expandedRecipes.splice(index, 1);
      } else {
        this.expandedRecipes.push(recipeId);
      }
    }
  }
};
</script>

<style scoped>
.home-page {
  text-align: center;
  padding: 20px;
}

.title {
  font-size: 2em;
  margin-bottom: 20px;
}

.message {
  margin-bottom: 20px;
  font-size: 1.2em;
}

.recipe-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  width: 80%;
  max-width: 600px;
  text-align: left;
  transition: transform 0.2s;
}

.recipe-card:hover {
  transform: translateY(-5px);
}

.recipe-image {
  background-size: cover;
  background-position: center;
  height: 200px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.recipe-title {
  background: rgba(0, 0, 0, 0.5);
  width: 100%;
  text-align: center;
  padding: 10px;
}

.recipe-title h3 {
  margin: 0;
  color: #fff;
}

.recipe-details {
  padding: 20px;
}

.recipe-details strong {
  display: block;
  margin-bottom: 10px;
  font-size: 1.2em;
}

.toggle-ingredients-btn {
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 10px;
}

.toggle-ingredients-btn:hover {
  /*background: rgb(43, 43, 44);*/
  background: #729874;
  color: #fff;
}

.recipe-details ul {
  list-style: none;
  padding: 0;
}

.recipe-details li {
  background: #f7f7f7;
  margin: 5px 0;
  padding: 10px;
  border-radius: 5px;
}
</style>
