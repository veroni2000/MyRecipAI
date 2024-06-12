<template>
  <div v-if="user">
    <div class="profile-container">
      <div class="profile-header">
        <img v-if="user.image" :src="require(`../../../main/resources/images/${user.image}`)"
             :alt="user.image" class="profile-pic">
        <img v-else :src="require(`../../../main/resources/images/default.png`)"
             alt="default profile picture" class="profile-pic">
        <div class="profile-info">
          <h2>{{ user.firstName }} {{ user.lastName }}</h2>
          <div class="stats">
            <span>{{ recipes.length }} recipes</span>
            <span>{{ followersCount }} followers</span>
            <span>{{ followingCount }} following</span>
          </div>
        </div>
        <div class="profile-actions">
          <div v-if="isCurrentUser" class="edit-profile-link">
            <router-link to="/user/edit" v-if="isCurrentUser"><i class="fas fa-edit"></i>edit profile</router-link>
          </div>
          <div v-else>
            <button v-if="showFollow" @click="followUser" class="follow-button">Follow</button>
            <button v-else @click="unfollowUser" class="follow-button">Unfollow</button>
          </div>
        </div>
      </div>
      <div v-if="recipes.length > 0" class="recipe-list">
        <h3>Recipes:</h3>
        <div class="recipes">
          <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card">
            <router-link :to="{ name: 'recipe', params: { recipeId: recipe.id }}">
              <div class="recipe-image" :style="{
                backgroundImage: recipe.image ? `url(${require('../../../main/resources/images/' + recipe.image)})` : `url(${require('../../../main/resources/images/defaultRecipe.png')})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
              }"></div>
            </router-link>
            <div class="recipe-info">
              <h3>{{ recipe.title }}</h3>
              <button @click="toggleIngredients(recipe.id)" class="toggle-ingredients-btn">
                {{ recipe.showIngredients ? 'Hide Ingredients' : 'Show Ingredients' }}
              </button>
              <div v-if="recipe.showIngredients">
                <strong>Ingredients:</strong>
                <ul class="ingredient-list">
                  <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient-item">
                    {{ ingredientItem.ingredient.ingredient }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/services/apiClient";
import axios from "axios";

export default {
  name: "UserProfileComponent",
  data() {
    return {
      user: {},
      recipes: [],
      showFollow: true,
      followersCount: 0,
      followingCount: 0,
    };
  },
  computed: {
    isCurrentUser() {
      const userIdFromParams = this.$route.params.userId;
      const loggedInUserId = localStorage.getItem("id");
      return userIdFromParams === loggedInUserId;
    }
  },
  mounted() {
    this.fetchUserDetails();
    this.fetchRecipesByUser(this.$route.params.userId);
    this.checkFollowing();
    this.getAllFollowingUser();
    this.getAllFollowedByUser();
  },
  methods: {
    fetchUserDetails() {
      const userId = this.$route.params.userId;

      axiosInstance.get(`/user/${userId}`)
          .then(response => {
            this.user = response.data;
          })
          .catch(error => {
            console.error('Error fetching user details:', error);
          });
    },
    fetchRecipesByUser(userId) {
      axiosInstance.get(`/recipe/byUser?userId=${userId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            this.recipes = response.data.map(recipe => ({
              ...recipe,
              showIngredients: false
            }));
          })
          .catch(error => {
            console.error('Error fetching recipes by user:', error);
          });
    },
    toggleIngredients(recipeId) {
      const recipe = this.recipes.find(recipe => recipe.id === recipeId);
      recipe.showIngredients = !recipe.showIngredients;
    },
    logout() {
      localStorage.clear();
      this.$router.push({ name: 'login' });
    },
    checkFollowing() {
      axios.get(`/api/follow/check?followedId=${this.$route.params.userId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(response => {
            if (response.data){
              this.showFollow = false;
            }
          })
          .catch(error => {
            console.error('Error:', error);
          });
    },
    followUser(){
      axiosInstance.post(`/follow`, {"followedId": this.$route.params.userId, "followingId": localStorage.getItem('id')})
          .then(() => {
            this.showFollow = false;
            this.getAllFollowingUser(); // Update follower count
          })
          .catch(error => {
            console.error('Error following:', error);
          });
    },
    unfollowUser(){
      axiosInstance.delete(`/follow`, {data:{"followedId": this.$route.params.userId, "followingId": localStorage.getItem('id')}})
          .then(() => {
            this.showFollow = true;
            this.getAllFollowingUser(); // Update follower count
          })
          .catch(error => {
            console.error('Error unfollowing:', error);
          });
    },
    getAllFollowingUser() {
      const userId = this.$route.params.userId;
      axiosInstance.get(`/follow/getAllFollowers?userId=${userId}`)
          .then(response => {
            this.followersCount = response.data.length;
          })
          .catch(error => {
            console.error('Error fetching follow counts:', error);
          });
    },
    getAllFollowedByUser() {
      const userId = this.$route.params.userId;
      axiosInstance.get(`/follow/getAllFollowedBy?userId=${userId}`)
          .then(response => {
            this.followingCount = response.data.length;
          })
          .catch(error => {
            console.error('Error fetching follow counts:', error);
          });
    }
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 800px;
  margin-bottom: 20px;
}

.profile-pic {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20px;
}

.profile-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left;
}

.profile-info h2 {
  margin: 0;
  margin-bottom: 10px;
  font-size: 2em;
}

.stats {
  display: flex;
  gap: 20px;
  font-size: 1.2em;
}

.profile-actions {
  display: flex;
  align-items: center;
}

.edit-profile-link {
  margin-left: auto;
}

.follow-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 10px;
}

.recipe-list {
  width: 100%;
}

.recipes {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.recipe-card {
  width: calc(33.333% - 20px);
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  text-align: center;
}

.recipe-card:hover {
  transform: translateY(-5px);
  transition: transform 0.3s ease-in-out;
}

.recipe-image {
  width: 100%;
  padding-top: 75%;
  background-size: cover;
  background-position: center;
}

.recipe-info {
  padding: 15px;
}

.toggle-ingredients-btn {
  padding: 10px 20px;
  background-color: #28a745;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.ingredient-list {
  list-style: none;
  padding: 0;
  margin: 10px 0 0 0;
}

.ingredient-item {
  list-style-type: none;
}

</style>