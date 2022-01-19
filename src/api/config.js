import axios from 'axios';

const axios_session = axios.create({ baseURL: 'http://localhost:8080' })

export { axios_session }
