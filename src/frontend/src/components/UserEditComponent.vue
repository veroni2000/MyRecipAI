<template>
  <div class="edit-profile-container">
    <h2>Edit User Profile</h2>
    <form @submit.prevent="updateUser" class="edit-profile-form">
      <div class="form-group">
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" v-model="user.firstName" required>
      </div>
      <div class="form-group">
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" v-model="user.lastName" required>
      </div>
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="user.email" required>
      </div>
      <div class="form-group">
        <label>Upload image:</label>
        <input type="file" accept="image/*" ref="uploadImage" @change="onImageUpload"/>
      </div>
      <div v-if="errorMessage" class="alert alert-danger" role="alert">
        {{ errorMessage }}
      </div>
      <div class="form-actions">
        <button type="submit" class="btn btn-primary">Save Changes</button>
        <router-link :to="'/user/' + user.id" class="btn btn-secondary">Cancel</router-link>
      </div>
    </form>
  </div>
</template>


<script>
import axiosInstance from "@/services/apiClient";
import axios from "axios";

export default {
  name: "UserEditComponent",
  data() {
    return {
      user: {},
      errorMessage: null
    };
  },
  mounted() {
    this.fetchUserDetails();
  },
  methods: {
    fetchUserDetails() {
      const userId = localStorage.getItem('id');

      axiosInstance.get(`/user/${userId}`)
          .then(response => {
            this.user = response.data;
          })
          .catch(error => {
            console.error('Error fetching user details:', error);
          });
      console.log(this.user)
    },
    async updateUser() {
      await this.fileUpload();
      await axiosInstance.post(`/user/edit`, this.user)
          .then(response => {
            // Optionally handle success response
            console.log('User profile updated:', response.data);
            window.location.href = `/user/${localStorage.getItem('id')}`;
          })
          .catch(error => {
            console.error('Error updating user profile:', error);
          });
    },
    onImageUpload() {
      let file = this.$refs.uploadImage.files[0];
      this.formData = new FormData();
      this.formData.append("file", file);
    },
    async fileUpload() {
      if (this.formData != null) {
        await axios.post(`/api/user/saveImage`, this.formData, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
            'Content-Type': 'multipart/form-data' // Set Content-Type to multipart/form-data
          }
        })
            .then(response => {
              console.log('Image saved successfully:', response.data);
              this.user.image = response.data;
              console.log("User Profile IMAGE NAME: " + this.user.image);
              // Handle response data as needed
            })
            .catch(error => {
              console.error('Error saving image:', error);
              // Handle error if the request fails
            });
      }
    }
  }
}
</script>

<style scoped>
.edit-profile-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.edit-profile-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1em;
}

.alert {
  margin-bottom: 15px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  text-decoration: none;
  text-align: center;
}

.btn-secondary:hover {
  background-color: #5a6268;
}
</style>


