<template>
  <div class="recipe-details">
    <div v-if="recipe" class="recipe-container">
      <div class="images">
        <img v-if="imageUrl" :src=imageUrl class="recipe-image">
        <img v-else-if="recipe.image" :src="require(`../../../main/resources/images/${recipe.image}`)"
             :alt="recipe.image"
             class="recipe-image"/>
        <img v-else :src="require(`../../../main/resources/images/defaultRecipe.png`)"
             alt="default recipe picture" class="recipe-image">
      </div>
      <div v-if="recipe" class="details">
        <router-link class="edit-link" v-if="showEdit" :to="'/recipe/edit/' + recipe.id"><i class="fas fa-edit"></i>edit</router-link>
        <div class="title">
          <h2 class="title">{{ recipe.title }}
            <MDBBadge v-if="recipe.aiGenerated" color="primary">AI</MDBBadge>
          </h2>
        </div>
        <div v-if="recipe.recipeTime" class="detail-item">
          <i class="fas fa-clock"></i>
           {{ recipe.recipeTime }}
        </div>
        <div v-if="recipe.recipeCalories" class="detail-item">
          <i class="fas fa-utensils"></i>
          {{ recipe.recipeCalories }} Cal
        </div>
        <div v-if="recipe.recipePrice" class="detail-item">
          <i class="fas fa-euro-sign"></i>
          {{ recipe.recipePrice }}
        </div>
        <div class="created-by">
          <strong>Created By: </strong>
          <router-link :to="profileLink" class="created-by-link">
            {{ recipe.createdBy.firstName + ' ' + recipe.createdBy.lastName }}
          </router-link>
        </div>
        <div class="ingredients">
          <strong>Ingredients:</strong>
          <div class="toggle-switch">
            <label>
              Display in
              <select v-model="displayMode">
                <option value="weight">Weight</option>
                <option value="volume">Volume</option>
              </select>
            </label>
          </div>
          <ul class="ingredient-list">
            <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient">
              <i class="fas fa-cookie"></i> {{ ingredientItem.ingredient.ingredient }} {{ displayIngredientValue(ingredientItem) }}
            </li>
          </ul>
        </div>
        <div class="instructions">
          <strong>Instructions:</strong>
          <pre>{{ recipe.instructions }}</pre>
        </div>
        <div v-if="loggedIn">
          <button v-if="showLike" @click="likeRecipe">Like</button>
          <button v-else @click="unlikeRecipe">Unlike</button>
        </div>
      </div>
    </div>
    <div v-else class="loading">
      <i class="fas fa-cookie fa-7x fa-spin" style="color: #e6b18e; margin-top: 5rem">
        <MDBSpinner style="margin-top: 25rem"/>
      </i>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import {MDBBadge} from "mdb-vue-ui-kit";
import axiosInstance from "@/components/apiClient";
import {MDBSpinner} from "mdb-vue-ui-kit";

export default {
  name: 'RecipePageComponent',
  components: {
    MDBBadge,
    MDBSpinner
  },
  data() {
    return {
      recipe: null,
      createdBy: {
        id: null,
        firstName: null,
        lastName: null,
      },
      displayMode: 'weight', // Default display mode
      showLike: true,
      loggedIn: true,
      showEdit: false,
      imageUrl: '',
    };
  },
  mounted() {
    this.fetchRecipeDetails();
    this.isLoggedIn();
    if (this.loggedIn)
      this.checkLike();
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
    async checkCurrentUser() {
      //await this.fetchRecipeDetails(); // Ensure recipe data is fetched

      if (this.recipe && this.recipe.createdBy) {
        const userIdFromRecipe = this.recipe.createdBy.id;
        const loggedInUserId = localStorage.getItem("id");
        if (userIdFromRecipe == loggedInUserId)
          this.showEdit = true;
        else this.showEdit = false;
      }
    },
    async fetchRecipeDetails() {
      if (localStorage.getItem('useUrl')) {
        this.imageUrl = localStorage.getItem('imageUrl');
        localStorage.removeItem('useUrl');
      }
      const recipeId = this.$route.params.recipeId;

      await axios.get(`/api/public/recipe/${recipeId}`)
          .then(response => {
            this.recipe = response.data;
            // Set default display mode based on recipe defaultMetric
            this.displayMode = this.recipe.defaultMetric === 'volume' ? 'volume' : 'weight';
          })
          .catch(error => {
            console.error('Error fetching recipe details:', error);
          });
      await this.checkCurrentUser();
    },
    displayIngredientValue(ingredientItem) {
      return this.displayMode === 'weight' ? ingredientItem.weight : ingredientItem.volume;
    },
    isLoggedIn() {
      if (!localStorage.getItem('jwtToken')) {
        this.loggedIn = false;
      }
    },
    likeRecipe() {
      axiosInstance.post(`/like`, {"recipeId": this.$route.params.recipeId, "userId": localStorage.getItem('id')})
          .then(() => {
            this.showLike = false;
          })
          .catch(error => {
            console.error('Error for like:', error);
          });
    },
    unlikeRecipe() {
      axiosInstance.delete(`/like`, {
        data: {
          "recipeId": this.$route.params.recipeId,
          "userId": localStorage.getItem('id')
        }
      })
          .then(() => {
            this.showLike = true;
          })
          .catch(error => {
            console.error('Error for unlike:', error);
          });
    },
    checkLike() {
      axios.get(`/api/like/check?recipeId=${this.$route.params.recipeId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            if (response.data) {
              this.showLike = false;
            }
          })
          .catch(error => {
            console.error('Error:', error);
          });
    },
  },
};
</script>

<style scoped>
.recipe-container {
  display: flex; /* Use flexbox layout */
  background-color: rgba(225, 250, 227, 0.76);
  max-width: 80%;
  padding: 3%;
  margin: auto; /* Center align the container */
  position: relative;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.images {
  flex: 1; /* Let the image take up available space */
  margin-right: 2%; /* Adjust margin as needed */
}

.recipe-image {
  width: 100%; /* Ensure the image takes up full width */
  height: auto; /* Maintain aspect ratio */
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.details {
  font-size: large;
  font-family: "Arial",serif;
  flex: 2; /* Let the details take up more space */
  padding-left: 20px;
}

.detail-item {
  display: inline-flex;
  align-items: center;
  margin-right: 20px; /* Add margin between detail items */
  margin-bottom: 10px;
}

.title {
  margin-bottom: 20px;
}

.created-by-link {
  text-decoration: none;
  color: #007bff;
  margin-bottom: 20px;
}

.ingredient-list {
  list-style-type: none;
  padding: 0;
}

.ingredient {
  margin-bottom: 10px;
}

pre {
  white-space: pre-wrap;
}

.edit-link {
  position: absolute;
  top: 10px;
  right: 20px;
  text-decoration: none;
  color: #007bff;
}
</style>
