import axios from 'axios';

const axios_session = axios.create({ baseURL: 'http://localhost:8080' })
axios_session.defaults.withCredentials = true;

export { axios_session }
