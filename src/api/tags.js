import { fakePromise } from './utils'


function getTags(user) {
  // if the user is logged in, get their secret tags
  if (user) {
    return fakePromise([{
      id: "a",
      name: "a",
      votes: 100,
      items: 200,
      creator: "tommy",
    },
    {
      id: "b",
      name: "b",
      votes: 3,
      items: 50,
      creator: "jake"
    },
    {
      id: "c",
      name: "secret",
      votes: 3,
      items: 50,
      creator: "jake"
    }
    ])
  }
  return fakePromise([{
    id: "a",
    name: "a",
    votes: 100,
    items: 200,
    creator: "tommy",
  },
  {
    id: "b",
    name: "b",
    votes: 3,
    items: 50,
    creator: "jake"
  }
  ])
}

function getTagById(tagId) {
  return fakePromise({
    id: tagId,
    name: tagId,
    description: "some tag description",
    creator: "a",
    votes: [
      {
        voter: "a",
        item: "arst",
        vs: "asdf",
        score: 1,
      }
    ],
    contributors: ["a", "b"],
    items: {
      ranked: [
        {
          score: 1,
          votes: 3,
          name: "arst",
          creator: "a"
        },
        {
          score: 1,
          votes: 3,
          name: "asdf",
          creator: "b"
        },
      ],
    unranked: [
      {
        score: 1,
        votes: 3,
        name: "poo",
        creator: "b"
      },
    ]}
  })
}

// get the two things to vote on next
function getNextVote(tagId) {
  return fakePromise([
    {
      type: "text",
      name: "item1"
    },
    {
      type: "text",
      name: "item2"
    }
  ])
}

export { getTags, getTagById, getNextVote }
