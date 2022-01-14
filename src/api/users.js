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
