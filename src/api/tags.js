var config = require('./config');

const getFrontpageTags = async (axios_session) => {
	// returns List[Tag]
	var resp = (await axios_session.get('')).data
	return resp.public.map(data => new Tag(data));
}

const getMyTags = async (axios_session) => {
	// returns List[Tag]
	return (await axios_session.get('api/tags/mine')).data.map(data => new Tag(data));
}

const getTagById = async (axios_session, tagId) => {
	return new Tag((await axios_session.get('api/tags/' + tagId)).data)
}

const createTag = async (axios_session, title, description) => {
	const format = {
		name:true,
		url:{
			youtube:false,
			"youtube with timestamp":false,
			spotify:false,
			"any website":true,
			"image link":false,
			twitter:false
		},
		paragraph:false
	}
	const data = {
		title: title,
		description: description,
		permissions: {
			perms: {},
			users: []
		},
		format: format,
	}
	var new_tag = await axios_session.post('api/tags', data)
	return new Tag(new_tag.data)
}

class Tag {
	constructor(json) {
		this.json = json
	}

	async delete(axios_session) {
		await axios_session.delete('api/tags/' + this.json.id)
	}

	async edit(axios_session, title, description, permissions) {
		const data = {
			title,
			description,
			// TODO permissions should be empty, cause 
			permissions
		}
		// TODO delete this from here and server, should not be able to edit format, but can edit settings
		try {
			var new_json = await axios_session.put('api/tags/' + this.json.id, data);
			this.json = new_json.data;
		} catch (error) {
			console.log('error', error);
		}
	}
}

// get all the tags
function getTags() {
	return [
		{
			id: "a",
			name: "a"
		},
		{
			id: "b",
			name: "b"
		}
	]
}


export { getTags, getTagById, getFrontpageTags, getMyTags, createTag }
