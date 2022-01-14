/*** @jest-environment node */
const tags = require("./tags");

import { axios_session, login } from './config';

test('can find my tags', async () => {
	var session = await login('tommy', 'happysnow');
	var data = await tags.getMyTags(session);

	expect(data.length >= 20).toBe(true)
})

test('can find frontpage tags', async () => {
	var session = await login('tommy', 'happysnow');

	var data = await tags.getFrontpageTags(session);

	expect(data[0].json.id.length > 20).toBe(true)
})

test('can get tag by ID', async () => {
	var session = await login('tommy', 'happysnow');

	var fronttags = await tags.getFrontpageTags(session);
	var first_id = fronttags[0].json.id;

	var first_tag = await tags.getTagById(session, first_id);

	expect(first_tag.json.title.length > 0).toBe(true)
	expect(first_tag.json.description.length > 0).toBe(true)
	//expect(data.private.length + data.public.length >= 20).toBe(true)
})

test('can create tag', async () => {
	var session = await login('tommy', 'happysnow');

	var first_tag = await tags.createTag(session, 'example tag', 'example description');

	expect(first_tag.json.title).toBe('example tag')
	expect(first_tag.json.description).toBe('example description')

	try {
	await first_tag.delete(session)
	} catch(error) {
		console.log('ero, ', error)
	}
	//expect(data.private.length + data.public.length >= 20).toBe(true)
})

test('can edit tag', async () => {
	var session = await login('tommy', 'happysnow');

	var first_tag = await tags.createTag(session, 'example tag', 'example description');

	expect(first_tag.json.title).toBe('example tag')

	const perms = { perms: {}, users: []};
	
	await first_tag.edit(session, 'example tag modified', 'example description modified', perms)
	expect(first_tag.json.title).toBe('example tag modified')
	expect(first_tag.json.description).toBe('example description modified')

	var first_tag_v2 = await tags.getTagById(session, first_tag.json.id)
	expect(first_tag_v2.json.title).toBe('example tag modified')
	expect(first_tag_v2.json.description).toBe('example description modified')

	//expect(data.private.length + data.public.length >= 20).toBe(true)
})
