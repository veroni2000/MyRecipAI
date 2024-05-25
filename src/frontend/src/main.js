import 'mdb-vue-ui-kit/css/mdb.min.css'
import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import axios from 'axios'; // Import axios
import App from './App.vue';
import RegistrationComponent from "@/components/RegistrationComponent";
import LoginComponent from './components/LoginComponent.vue';
import homePageComponent from "@/components/HomePageComponent";
import userProfileComponent from "@/components/UserProfileComponent";
import VerifyUserComponent from "@/components/VerifyUserComponent";
import UserEditComponent from "@/components/UserEditComponent";
import AddRecipeComponent from "@/components/AddRecipeComponent";
import CalculatorComponent from "@/components/CalculatorComponent";
import EmailComponent from "@/components/EmailComponent";
import ResetPasswordComponent from "@/components/ResetPasswordComponent";
import AddIngredientComponent from "@/components/AddIngredientComponent";
import recipePageComponent from "@/components/RecipePageComponent";
import recipeEditComponent from "@/components/RecipeEditComponent";
import recipeModifyComponent from "@/components/RecipeModifyComponent";
import imageUploadComponent from "@/components/ImageUploadComponent";
import generateRecipeComponent from "@/components/GenerateRecipeComponent";
import searchRecipeComponent from "@/components/SearchRecipeComponent";
import searchRecipesByIngredientComponent from "@/components/SearchRecipesByIngredientComponent";

// Create the router instance
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'home', component: homePageComponent },
        { path: '/login', name: 'login', component: LoginComponent },
        { path: '/register', name: 'register', component: RegistrationComponent },
        { path: '/verify', name: 'verify', component: VerifyUserComponent },
        { path: '/user/:userId', name: 'user', component: userProfileComponent },
        { path: '/user/edit', name: 'userEdit', component: UserEditComponent },
        { path: '/addRecipe', name: 'addRecipe', component: AddRecipeComponent },
        { path: '/addIngredient', name: 'addIngredient', component: AddIngredientComponent},
        { path: '/calculator', name: 'calculator', component: CalculatorComponent },
        { path: '/forgotten', name: 'forgotten', component: EmailComponent },
        { path: '/reset', name: 'reset', component: ResetPasswordComponent },
        { path: '/recipe/:recipeId', name: 'recipe', component: recipePageComponent },
        { path: '/recipe/edit/:recipeId', name: 'recipeEdit', component: recipeEditComponent },
        { path: '/recipe/modify/:recipeId', name: 'recipeModify', component: recipeModifyComponent },
        { path: '/image', name: 'image', component: imageUploadComponent },
        { path: '/generate', name: 'generate', component: generateRecipeComponent },
        { path: '/search/:msg', name: 'search', component: searchRecipeComponent },
        { path: '/searchIng/:ingId/:ingName', name: 'searchByIngredient', component: searchRecipesByIngredientComponent },
    ]
});

// Add axios interceptor
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response.status === 403) {
            // Redirect to the forbidden page
            router.push('/login');
        }
        return Promise.reject(error);
    }
);

// Create the Vue app instance
const app = createApp(App);

// Use the router with the app
app.use(router);

// Mount the app to the element with the ID 'app'
app.mount('#app');
