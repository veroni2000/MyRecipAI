<template>
  <div class="search-container">
    <input
        type="text"
        v-model="ingredient"
        @input="handleInput"
        placeholder="Enter ingredient"
        class="form-control"
    >
    <ul v-if="showResults" class="search-results">
      <li v-for="result in results" :key="result.id" @click="selectIngredient(result)">
        {{ result.ingredient }}
      </li>
      <li v-if="showAddButton" @click="addNewIngredient">
        Add "{{ ingredient }}"
      </li>
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
      addButtonClicked: false  // Flag to track if the add button is clicked
    };
  },
  computed: {
    showAddButton() {
      // Check if the input exactly matches any of the results and the button is not already clicked
      return this.results.every(result => result.ingredient.toLowerCase() !== this.ingredient.toLowerCase().trimEnd()) && !this.addButtonClicked;
    }
  },
  methods: {
    handleInput() {
      // Reset the addButtonClicked flag when input changes
      this.addButtonClicked = false;
      this.searchIngredients();  // Trigger search when input changes
    },
    searchIngredients() {
      this.$emit('ingredient-selected', '');
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
            this.$emit('ingredient-selected', this.ingredient);
            this.addButtonClicked = true;  // Set the flag to true after the button is clicked
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

<style scoped>
.search-container {
  position: relative;
}

.search-container input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #fff;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 5px;
  padding: 0;
  list-style: none;
}

.search-results li {
  padding: 10px;
  cursor: pointer;
}

.search-results li:hover {
  background-color: #f0f0f0;
}
</style>
