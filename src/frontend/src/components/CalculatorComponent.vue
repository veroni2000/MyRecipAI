<template>
  <div>
    <h2>Conversion Calculator</h2>
    <div class="form-group">
      <label for="conversionType">Conversion Type:</label>
      <select v-model="conversionType" class="form-control" id="conversionType">
        <option value="gramsToCups">Grams to Cups</option>
        <option value="cupsToGrams">Cups to Grams</option>
      </select>
    </div>

    <div class="form-group">
      <label for="ingredient">Ingredient:</label>
      <input type="text" v-model="ingredient" class="form-control" id="ingredient" placeholder="Enter ingredient">
    </div>

    <div class="form-group">
      <label for="inputValue">Value to Convert:</label>
      <div class="input-group">
        <input type="text" v-model="inputValue" class="form-control" id="inputValue" placeholder="Enter value">
        <div class="input-group-append">
          <button type="button" class="btn btn-primary" @click="convert">Convert</button>
        </div>
      </div>
    </div>

    <div v-if="convertedValue !== null">
      <p>{{ inputValue }} {{ ingredient }} = {{ convertedValue }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "CalculatorComponent",
  data() {
    return {
      conversionType: "gramsToCups",
      ingredient: "",
      inputValue: "",
      convertedValue: null,
      msg: ''
    };
  },
  methods: {
    convert() {
      this.msg = `${this.ingredient} ${this.inputValue}`;
      const assistant = this.conversionType;
      axios.get(`/api/public/${assistant}?msg=${this.msg}`)
            .then(response => {
              this.convertedValue = response.data;
              console.log(this.convertedValue)
            })
            .catch(error => {
              console.error('Error', error);
            });
        return this.convertedValue;
    }
  }
};
</script>

<style scoped>
</style>
