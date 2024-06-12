<template>
  <div class="calculator">
    <h2>Conversion Calculator</h2>
    <div className="col-sm-6 mx-auto">
      <div className="form-group">
        <label htmlFor="conversionType">Conversion Type:</label>
        <select v-model="conversionType" className="form-control" id="conversionType">
          <option value="gramsToCups">Weight to Volume</option>
          <option value="cupsToGrams">Volume to Weight</option>
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="ingredient">Ingredient:</label>
        <input type="text" v-model="ingredient" className="form-control" id="ingredient" placeholder="Enter ingredient"
               @input="validateIngredient">
      </div>

      <div className="form-group">
        <label htmlFor="inputValue">Amount to Convert:</label>
        <div className="input-group">
          <input type="text" v-model="inputValue" className="form-control" id="inputValue" placeholder="Enter amount"
                 @input="validateInputValue">
        </div>
      </div>
      <br>
      <div v-if="isLoading">
        <!--        <i class="fas fa-cookie-bite fa-3x fa-spin" style="color: #e6b18e"></i>-->
        <p>Loading</p>
        <i class="fas fa-cookie fa-7x fa-spin" style="color: #e6b18e; margin-top: 5rem">
          <MDBSpinner style="margin-top: 25rem"/>
        </i>
      </div>
      <div v-else>
        <button type="button" className="btn btn-primary" @click="convert" :disabled="!allFieldsFilled()">Convert
        </button>
      </div>
    </div>

    <div v-if="displayError">
      <p>Something went wrong. Please check your input and make sure it contains food ingredients only.</p>
    </div>
    <div v-else-if="convertedValue !== null" style="margin-top: 10px;">
      <strong> {{ msg }} = {{ convertedValue }} </strong>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import {MDBSpinner} from "mdb-vue-ui-kit";

export default {
  name: "CalculatorComponent",
  components: {
    MDBSpinner,
  },
  data() {
    return {
      conversionType: "gramsToCups",
      ingredient: "",
      inputValue: "",
      convertedValue: null,
      msg: '',
      isLoading: false,
      displayError: false,
    };
  },
  methods: {
    convert() {
      this.convertedValue = null;
      this.isLoading = true;
      this.displayError = false;
      this.msg = `${this.ingredient}: ${this.inputValue}`;
      const assistant = this.conversionType;
      axios.get(`/api/public/${assistant}?msg=${this.msg}`)
          .then(response => {
            console.log(response.data);
            if (response.data === '0') {
              this.displayError = true;
            } else if (response.data.toLowerCase().includes('sorry')) {
              this.displayError = true;
            } else {
              this.convertedValue = response.data;
              this.clearInputs();
            }
          })
          .catch(error => {
            console.error('Error', error);
          })
          .finally(() => {
            this.isLoading = false;
          });
    },
    allFieldsFilled() {
      return this.ingredient !== "" && this.inputValue !== "";
    },
    clearInputs() {
      this.ingredient = "";
      this.inputValue = "";
    },
    validateIngredient(event) {
      const inputValue = event.target.value;
      const validatedValue = inputValue.replace(/[^a-zA-Z\s-]/g, '');
      this.ingredient = validatedValue;
    },
    validateInputValue(event) {
      const inputValue = event.target.value;
      const validatedValue = inputValue.replace(/[^a-zA-Z0-9.\s-]/g, '');
      this.inputValue = validatedValue;
    }
  }
};
</script>

<style scoped>
.calculator {
  max-width: 60%;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
