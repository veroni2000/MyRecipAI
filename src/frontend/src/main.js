import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import RegistrationComponent from "@/components/RegistrationComponent";
import LoginComponent from './components/LoginComponent.vue';
import homePageComponent from "@/components/HomePageComponent";
import userProfileComponent from "@/components/UserProfileComponent";

// Create the router instance
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'home', component: homePageComponent },
        { path: '/login', name: 'login', component: LoginComponent },
        { path: '/register', name: 'register', component: RegistrationComponent },
        { path: '/user/:userId', name: 'user', component: userProfileComponent },
    ]
});

// Create the Vue app instance
const app = createApp(App);

// Use the router with the app
app.use(router);

// Mount the app to the element with the ID 'app'
app.mount('#app');
