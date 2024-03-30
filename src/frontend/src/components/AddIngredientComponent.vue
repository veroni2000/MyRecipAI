<template>
  <div class="search-container">
    <input type="text" v-model="ingredient" @input="searchIngredients" placeholder="Enter ingredient">
    <ul v-if="showResults" class="search-results">
      <li v-for="result in results" :key="result.id" @click="selectIngredient(result)">
        {{ result.ingredient }}
      </li>
      <li v-if="showAddIngredientButton" @click="addNewIngredient">Add "{{ ingredient }}"</li>
    </ul>

  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "AddIngredientComponent",
  data() {
    return {
      ingredient: '',
      results: [],
      showResults: false,
      ingredientExists: false,
      showAddButton: false
    };
  },
  computed: {
    showAddIngredientButton() {
      return this.results.length === 0;
    }
  },
  methods: {
    searchIngredients() {
      if (this.ingredient.length >= 3) {
        axios.get(`/api/ingredient?name=${this.ingredient}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
          }
        })
            .then(response => {
              this.results = response.data;
              this.showResults = true;
            })
            .catch(error => {
              console.error('Error searching ingredients:', error);
            });
      } else {
        this.results = [];
        this.showResults = false;
      }
    },
    selectIngredient(selectedIngredient) {
      this.ingredient = selectedIngredient.ingredient;
      this.$emit('ingredient-selected', this.ingredient);
      this.showResults = false;
    },
    addNewIngredient() {
      axios.post('/api/ingredient', { ingredient: this.ingredient }, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
          .then(() => {
            console.log('Ingredient added successfully');
            this.$emit('ingredient-selected', this.ingredient);
          })
          .catch(error => {
            console.error('Error adding ingredient:', error);
          });
    },
    submitForm() {
      this.$emit('ingredient-selected', this.ingredient);
    }
  }
}
</script>
