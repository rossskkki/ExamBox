// src/api/exam.js
import axios from 'axios';

const request = axios.create({
  timeout: 10000
});

export const searchExam = (name) => request({
  url: '/exambase/exam/search',
  method: 'post',
  params: { name }
});

export const searchQ = (searchBody) => request({
  url: '/exambase/search',
  method: 'post',
  data: searchBody
});
