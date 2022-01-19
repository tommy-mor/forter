// import { fakePromise } from './utils'

const sessionToken = 0
const SORTER_LOGIN_TOKEN = 'sorter-login-token'

// add session cookie
function login(username, password) {
  console.log("logging in")
  document.cookie = `${SORTER_LOGIN_TOKEN}=${sessionToken};`
}

// expire session cookie
function logout() {
  console.log("logging out")
  document.cookie = `${SORTER_LOGIN_TOKEN}=; expires=Thu, 01 Jan 1970 00:00:01 GMT;`
}

// add session cookie with username and password
function register(username, password) {
  console.log("registering and logging in")
  document.cookie = `${SORTER_LOGIN_TOKEN}=${sessionToken};`
}

// get user info if the user is logged in
async function getUser() {

  await new Promise(res => setTimeout(res, 500))
  if (document.cookie.includes(`${SORTER_LOGIN_TOKEN}=${sessionToken}`)) {
    // authorized
    return {
      name: "Shu",
      avatar: "https://github.com/shuding.png"
    }
  }

  // not authorized
  // should probably be real error
  return "error"
}

export { login, logout, register, getUser }
