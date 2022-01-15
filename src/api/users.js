import { fakePromise } from './utils'

function getUsers() {
  return fakePromise([
    {
      name: "a"

    },
    {
      name: "b"
    }
  ])
}

function getUserById(userId) {
  return fakePromise({
    name: userId
  })
}

export { getUsers, getUserById }
