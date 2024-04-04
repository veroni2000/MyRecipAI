<template>
  <div>
    <div v-if="isCurrentUser" class="edit-profile-link">
      <router-link to="/user/edit">Edit profile</router-link>
    </div>
    <div class="user-profile">
      <h2>User Profile</h2>
      <div>
        <strong>Email:</strong> {{ user.email }}
      </div>
      <div>
        <strong>First Name:</strong> {{ user.firstName }}
      </div>
      <div>
        <strong>Last Name:</strong> {{ user.lastName }}
      </div>
      <button @click="logout" class="logout-button">Logout</button>
      <div v-if="recipes.length > 0" class="recipe-list">
        <h3>Recipes:</h3>
        <div v-for="recipe in recipes" :key="recipe.id" class="recipe-item">
          <div>
            <router-link :to="{ name: 'recipe', params: { recipeId: recipe.id }}" class="recipe-title">{{ recipe.title }}</router-link>
          </div>
          <div>
            <strong>Ingredients:</strong>
            <ul>
              <li v-for="(ingredientItem, index) in recipe.recipeIngredients" :key="index" class="ingredient-item">
                {{ ingredientItem.ingredient.ingredient }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/components/apiClient";

export default {
  name: "UserProfileComponent",
  data() {
    return {
      user: {},
      recipes: [],
    };
  },
  computed: {
    isCurrentUser() {
      const userIdFromParams = this.$route.params.userId;
      const loggedInUserId = localStorage.getItem("id");
      return userIdFromParams === loggedInUserId;
    },
  },
  mounted() {
    this.fetchUserDetails();
    this.fetchRecipesByUser(this.$route.params.userId);
  },
  methods: {
    fetchUserDetails() {
      const userId = this.$route.params.userId;

      axiosInstance.get(`/user/${userId}`)
          .then(response => {
            this.user = response.data;
            console.log(this.user)
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
            this.recipes = response.data;
          })
          .catch(error => {
            console.error('Error fetching recipes by user:', error);
          });
    },
    logout() {
      localStorage.clear();
      this.$router.push({ name: 'login' });
    }
  }
}
</script>

<style scoped>
.edit-profile-link {
  margin-bottom: 20px;
}

.user-profile {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.logout-button {
  margin-top: 10px;
  padding: 5px 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.recipe-list {
  margin-top: 20px;
}

.recipe-item {
  margin-bottom: 20px;
}

.recipe-title {
  font-size: 20px;
  color: #007bff;
}

.ingredient-list {
  list-style: none;
  padding-left: 0;
}

.ingredient-item {
  list-style-type: none;
}
</style>