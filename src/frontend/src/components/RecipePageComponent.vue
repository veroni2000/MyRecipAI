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
        <div class="like-container">
          <div v-if="loggedIn">
            <i class="far fa-heart fa-3x" id="like" v-if="showLike" @click="likeRecipe"></i>
            <i class="fas fa-heart fa-3x" id="unlike" v-else @click="unlikeRecipe" style="color: #cd7979"></i>
          </div>
          <div v-if="likes" class="likes">
            <i class="fas fa-heart" style="margin-right: 0;"></i>
            {{ likes }}
          </div>
        </div>
      </div>
      <div v-if="recipe" class="details">
        <router-link class="edit-link" v-if="showEdit" :to="'/recipe/edit/' + recipe.id"><i class="fas fa-edit"></i>edit
        </router-link>
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
            <div class="switch-container">
              <p style="display: inline-block; margin-right: 10px;">Volume</p>
              <MDBSwitch class="custom-control-input" id="measurementSwitch" v-model="isWeightMode"
                         @change="toggleDisplayMode"></MDBSwitch>
              <p style="display: inline-block; margin-left: 10px;">Weight</p>
            </div>
          </div>
          <table class="ingredients-table">
            <tbody>
            <tr v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index">
              <td><i class="fas fa-cookie"></i> {{ ingredientItem.ingredient.ingredient }}</td>
              <td>{{ displayIngredientValue(ingredientItem) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="instructions">
          <strong>Instructions:</strong>
          <pre>{{ recipe.instructions }}</pre>
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
import {MDBBadge, MDBSwitch} from "mdb-vue-ui-kit";
import axiosInstance from "@/components/apiClient";
import {MDBSpinner} from "mdb-vue-ui-kit";

export default {
  name: 'RecipePageComponent',
  components: {
    MDBBadge,
    MDBSpinner,
    MDBSwitch,
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
      likes: '',
    };
  },
  mounted() {
    this.fetchRecipeDetails();
    this.isLoggedIn();
    if (this.loggedIn)
      this.checkLike();
    this.getLikesCount();
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
            this.isWeightMode = this.displayMode === 'weight';
          })
          .catch(error => {
            console.error('Error fetching recipe details:', error);
          });
      await this.checkCurrentUser();
    },
    toggleDisplayMode() {
      this.displayMode = this.isWeightMode ? 'volume' : 'weight';
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
            this.getLikesCount();
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
            this.getLikesCount()
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
    getLikesCount() {
      axios.get(`/api/public/likes?recipeId=${this.$route.params.recipeId}`)
          .then(response => {
            if (response.data) {
              this.likes = response.data;
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
  background-color: #f9f9f9;
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

.like-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.details {
  font-size: large;
  font-family: "Arial", serif;
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

.ingredients {
  margin-top: 20px;
}

.toggle-switch {
  margin-top: 10px;
  margin-bottom: 10px;
}

.ingredients-table {
  width: 50%;
  border-collapse: collapse;
  margin-left: 25%;
  text-align: left;
}

.ingredients-table td {
  padding: 5px 10px;
}

.instructions {
  margin-top: 20px;
  width: 80%;
  margin-left: 10%;
  text-align: justify;
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

#like:hover, #unlike:hover {
  transform: translateY(-5px);
}

i {
  margin-right: 5px;
}
</style>
