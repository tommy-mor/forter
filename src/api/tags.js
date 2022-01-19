import { fakePromise } from './utils'
import { Sorter, Tag } from 'jorter/api/tags'
import { axios_session } from './config'



function getTags(user) {
	// if the user is logged in, get their secret tags
	return new Sorter().getFrontpageTags(axios_session)
}

function getTagById(id) {
	return new Tag({id}).get_sorted(axios_session)

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
