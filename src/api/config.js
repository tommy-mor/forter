const BASE_URL = "http://localhost:8080/";

var axios = require('axios');



const login = async (username, password) => {
	const axios_session = axios.create({ baseURL: BASE_URL });
	// https://gist.github.com/CITGuru/61dd109adc2be5d3fd6274f15123fe0e
	const data = {
		'username': username,
		'password': password,
	};

	const resp = await axios_session.post('api/user/login', data);
	const [cookie] = resp.headers["set-cookie"];
	axios_session.defaults.headers.Cookie = cookie;

	return axios_session;
}

// probably bad xd


export { login }
