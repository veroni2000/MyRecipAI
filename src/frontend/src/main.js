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
    ]
});

// Add axios interceptor
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response.status === 403) {
            // Redirect to the forbidden page
            router.push('/login'); // Customize the route as needed
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
