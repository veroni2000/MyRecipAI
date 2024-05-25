<template>
  <div class="recipe-card">
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
      <button @click="toggleIngredients" class="toggle-ingredients-btn">
        {{ showIngredients ? 'Hide Ingredients' : 'Show Ingredients' }}
      </button>
      <ul v-if="showIngredients" class="ingredient-list">
        <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient-item">
          <router-link :to="{ name: 'searchByIngredient', params: { ingId: ingredientItem.ingredient.id, ingName: ingredientItem.ingredient.ingredient }}" onclick="window.location.href=''">
            {{ ingredientItem.ingredient.ingredient }}
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { MDBBadge } from 'mdb-vue-ui-kit';

export default {
  name: 'RecipeComponent',
  components: {
    MDBBadge,
  },
  props: {
    recipe: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      showIngredients: false
    };
  },
  methods: {
    toggleIngredients() {
      this.showIngredients = !this.showIngredients;
    }
  }
};
</script>

<style scoped>
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

.toggle-ingredients-btn {
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 10px;
}

.toggle-ingredients-btn:hover {
  background: #729874;
  color: #fff;
}

.ingredient-list {
  display: flex;
  flex-wrap: wrap;
  padding: 0;
  margin: 0;
  list-style: none;
}

.ingredient-item {
  background: #f7f7f7;
  margin: 5px;
  padding: 10px;
  border-radius: 5px;
  white-space: nowrap; /* Ensure the items do not break to a new line */
}
</style>
