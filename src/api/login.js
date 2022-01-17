import { fakePromise } from './utils'

const sessionToken = 0
const SORTER_LOGIN_TOKEN = 'sorter-login-token'

// add session cookie
async function login(username, password) {
  document.cookie = `${SORTER_LOGIN_TOKEN}=${sessionToken};`
  return fakePromise({ username, password })
}

// expire session cookie
async function logout() {
  document.cookie = `${SORTER_LOGIN_TOKEN}=; expires=Thu, 01 Jan 1970 00:00:01 GMT;`
}

// add session cookie with username and password
async function register(username, password) {
  document.cookie = `${SORTER_LOGIN_TOKEN}=${sessionToken};`
  return fakePromise({ username, password })
}

// get user info if the user is logged in
async function getUser() {
  return new Promise(res => setTimeout(() => {
    if (document.cookie.includes(`${SORTER_LOGIN_TOKEN}=${sessionToken}`)) {
      // authorized
      return {
        name: "Shu",
        avatar: "https://github.com/shuding.png"
      }
    }

    // not authorized
    const error = new Error("Not authorized!")
    error.status = 403
    throw error
  }, 100))
}

export { login, logout, register, getUser }
