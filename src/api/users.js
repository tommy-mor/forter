var config = require('./config');

const register = async (username, password) => {
	const data = {
		'username': username,
		'password': password,
	};

	const resp = await axios_session.post('api/user/register', data);
}

// get all the users
function getUsers() {
  return [
    {
      name: "a"

    },
    {
      name: "b"

    }
  ]
}

function getUserById(userId) {
  return {
    name: userId
  }
}

export { getUsers, getUserById }
