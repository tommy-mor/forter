/* eslint-disable */
import React, { useState, useEffect, Fragment } from "react";

API_URL = "/priv/tags/new";

async function postData(url = "", data = {}) {
	// Default options are marked with *
	console.log('form data')
	console.log(data);
	console.log('url')
	console.log(url)
  const response = await fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, *cors, same-origin
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    credentials: "same-origin", // include, *same-origin, omit
    headers: {
      "Content-Type": "application/json"
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    redirect: "follow", // manual, *follow, error
    referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  })
	const json = await response.json()
	console.log('response')
	console.log(json)
	window.location.href = json.new_tag_url
}

export default function App() {
	if (typeof settings == 'undefined') {
		settings = {
			title: '', description: '',
			perms: {perms: {}},
			format: {...Formats, url: UrlFormats},
			submiturl: API_URL,
			editing: false
	    }
	}
	console.log('settings2')
	console.log(settings)

	return (
    <div className="App">
		<TagCreator initstate={settings}/>
    </div>
  );
}

const UrlFormats = {
  "any website": true,
  "image link": false,
  youtube: false,
  "youtube with timestamp": false,
  spotify: false,
  twitter: false
};


const Formats = {
  name: true,
  paragraph: false,
  url: null,
};

// items can be "owner", "anybody", "users", or a user ID

const Permissions = {
  view_tag: [],
  add_items: [],
  vote: []
};

dummyUserNames = ["tommy", "nate", "blobbed", "bobathan"];


function TagCreator({initstate}) {
  const [format, setFormat] = useState(Object.assign({}, initstate.format));
  const [urlFormat, setUrlFormat] = useState(Object.assign({}, initstate.format.url));
  const [title, setTitle] = useState(initstate.title);
  const [description, setDescription] = useState(initstate.description);
  const [permissions, setPermissions] = useState(
    initstate.perms.perms
  );
  const [listOfUsers, setListOfUsers] = useState([]);

  const handleChange = (field) => {
    setFormat({ ...format, [field]: !format[field] });
  };

  const handleUrlChange = (type) => {
    //if (type === "any website") {
      //setUrlFormat(
        //Object.keys(urlFormat).reduce(
          //(prev, type) => ({ ...prev, [type]: !urlFormat["any website"] }),
          //{}
        //),
        //() => console.log(urlFormat)
      //);
      //console.log(urlFormat);
    //} else if (!urlFormat["any website"]) {
	  setUrlFormat(
		  {...
		   Object.keys(urlFormat).reduce(
			   (prev, type) => ({ ... prev, [type]: false}), {}
		   ),
		   [type]: true}
	  );
    //}
  };

  const handleSubmit = () => {
    data = {
      title: title,
      description: description,
		permissions: {perms: permissions,
					  users: listOfUsers},
      format: { ...format, url: format.url ? UrlFormats : false }
    };
    //confirm("Create this tag?") ? postData(API_URL, data) : null;
	  if(initstate.editing) {
		  // tagid is global set by server in <script> tag
		  data.tag_id = tagid;
	  }
      postData(initstate.submiturl, data);
  };

  // this is weird IDK what else to do
  const inputListFromFormat = (format) => {
    return Object.keys(format).reduce(
      (list, field) => (format[field] ? [...list, field] : list),
      // start object, makes sure returns list
      []
    );
  };

  // debugging
  // useEffect(() => console.log(permissions), [permissions]);

  return (
    <div>
      <label htmlFor="title">Title:</label>
      <input
        type="text"
        name="title"
        value={title}
        onChange={(event) => setTitle(event.target.value)}
      />
      <br />
      <label htmlFor="description">description:</label>
      <input
        type="text"
        name="description"
        value={description}
        onChange={(event) => setDescription(event.target.value)}
      />
      <br />

          <label> Permissions: </label>
          <PermissionsPicker
            permissions={permissions}
            setPermissions={setPermissions}
            userNames={dummyUserNames}
		    listOfUsers={listOfUsers}
			setListOfUsers={setListOfUsers}
          />



   {initstate.editing ? null :  //you can't edit format
	<Fragment>
      <label> What the Add Item form Will Look Like </label>
      <div className="tag-creator">
        <ExampleItemCreator inputList={inputListFromFormat(format)} />
        <form>
          Name <br />
          <div onChange={() => handleChange("url")}>
            Url?:
            <input
              type="radio"
              name="url"
              value="Yes"
              checked={format["url"]}
            />
            <label htmlFor="Yes">Yes</label>
            <input
              type="radio"
              name="url"
              value="No"
              checked={!format["url"]}
            />
            <label htmlFor="No">No</label>
          </div>
          {format["url"]
            ? Object.getOwnPropertyNames(urlFormat).map((type) => (
                <Fragment key={type}>
                  <input
                    type="radio"
                    name={type}
                    value={type}
                    key={type}
                    checked={urlFormat[type]}
                    onChange={() => handleUrlChange(type)}
                  />
                  <label htmlFor={type}>{type}</label>
                  <br />
                </Fragment>
              ))
            : null}
          <div onChange={() => handleChange("paragraph")}>
            paragraph?:
            <input
              type="radio"
              name="paragraph"
              value="Yes"
              checked={format["paragraph"]}
            />
            <label htmlFor="Yes">Yes</label>
            <input
              type="radio"
              name="paragraph"
              value="No"
              checked={!format["paragraph"]}
            />
            <label htmlFor="No">No</label>
          </div>
        </form>
      </div>
		</Fragment>
   }
		{initstate.editing ?
		 <input type="submit" value="commit changes" onClick={handleSubmit} />
		 :
		 <input type="submit" value="create tag" onClick={handleSubmit} />
		}
      
    </div>
  );
}



function ItemCreator(props) {
	const [form, setForm] = useState({});
	const handleChange = (event, name) => setForm({...form, [name]: event.target.value})
	const value = (name) => form[name] ?? ''

	const handleSubmitItem = (event) => {
		event.preventDefault() //otherwise it refreshes page?
		frontsorter.core.add_item(form, ()=>setForm({}))
	}
   const RealInputs = {
       name: <input className="addinput" type="text" placeholder="item title"
					value={value("name")}
					onChange={(event) => handleChange(event, "name")}/>,
     // TODO put example url in placeholder
       url: <input className="addinput" type="url" placeholder="item url"
					value={value("url")}
					onChange={(event) => handleChange(event, "url")}/>,

       paragraph: <textarea className="addinput" placeholder="best song ever"
					value={value("paragraph")}
					onChange={(event) => handleChange(event, "paragraph")}/>,
  };
  return (
    <form>
      {props["inputList"].map((inputName) => (
        <Fragment key={inputName}>
          {RealInputs[inputName]}
          <br />
        </Fragment>
      ))}
		 <input type="submit" value="add item" onClick={handleSubmitItem} />
    </form>
  );
}

const ExampleInputs = {
  name: <input type="text" placeholder="song" disabled />,
  url: (
    <input
      type="url"
      placeholder="https://www.youtube.com/watch?v=dQw4w9WgXcQ"
      disabled
    />
  ),
  paragraph: <textarea placeholder="best song ever" disabled />
};

function ExampleItemCreator(props) {
  // console.log(props);
  return (
    <form>
      {props["inputList"].map((inputName) => (
        <Fragment key={inputName}>
          {ExampleInputs[inputName]}
          <br />
        </Fragment>
      ))}
    </form>
  );
}

function PermissionsPicker({ permissions, setPermissions, listOfUsers, setListOfUsers, userNames }) {

  //TODO: initialize form state from server
  // const permissionsFromPermissions = {
  // }

  // refactor or no?
  const handleChange = ({ target }) => {
    anybody = target.dataset.permission + "__anybody";
    users = target.dataset.permission + "__users";
    list = target.dataset.permission + "__list";

    if (target.dataset.usertype === "anybody") {
      setPermissions({
        ...permissions,
        [anybody]: target.checked,
        [users]: target.checked,
        [list]: target.checked
      });
    } else if (target.dataset.usertype === "users") {
      setPermissions({
        ...permissions,
          [users]:  permissions[anybody] || target.checked,
        [list]:  permissions[anybody] || target.checked
      });
    } else if (target.dataset.usertype === "list") {
      setPermissions({
        ...permissions,
        [list]: permissions[anybody] || permissions[users] || target.checked
      });
    }
  };

  const handleUserName = ({ target }) => {
    listOfUsers.includes(target.value)
      ? setListOfUsers(listOfUsers.filter((user) => user !== target.value))
      : setListOfUsers(listOfUsers.concat([target.value]));
  };

  const isChecked = (usertype, permission) => {
    return permissions[permission + "__" + usertype];
  };
  return (
    <form className="permissionform">
      Anyone on the Internet:
	  <br/>
      <input
        type="checkbox"
        data-usertype="anybody"
        data-permission="view_tag"
        checked={isChecked(["anybody"], "view_tag")}
        onChange={handleChange}
      />
      <label>view tag</label>
      <br />
      Any Sorter User:
	  <br/>
      <input
        type="checkbox"
        data-permission="view_tag"
        data-usertype="users"
        checked={isChecked(["users"], "view_tag")}
        onChange={handleChange}
      />
      <label>view tag</label>
      <input
        type="checkbox"
        data-permission="add_items"
        data-usertype="users"
        checked={isChecked(["users"], "add_items")}
        onChange={handleChange}
      />
      <label>add items</label>
      <input
        type="checkbox"
        data-permission="vote"
        data-usertype="users"
        checked={isChecked(["users"], "vote")}
        onChange={handleChange}
      />
      <label>vote</label>
      <br />
      Any User in this list: {listOfUsers.join(", ") + ", "}
      <select
        name="userName"
        id="userNameList"
        onChange={handleUserName}
        value="dummy"
      >
        <option value="dummy" hidden />
        {userNames.map((user) => (
          <option value={user} key={user}>
            {user}
          </option>
        ))}
      </select>
      <br />
      <input
        type="checkbox"
        data-permission="view_tag"
        data-usertype="list"
        checked={isChecked(["list"], "view_tag")}
        onChange={handleChange}
        disabled={!listOfUsers.length}
      />
      <label>view tag</label>
      <input
        type="checkbox"
        data-permission="add_items"
        data-usertype="list"
        checked={isChecked(["list"], "add_items")}
        onChange={handleChange}
        disabled={!listOfUsers.length}
      />
      <label>add items</label>
      <input
        type="checkbox"
        data-permission="vote"
        data-usertype="list"
        checked={isChecked(["list"], "vote")}
        onChange={handleChange}
        disabled={!listOfUsers.length}
      />
      <label>vote</label>
      <br />
    </form>
  );
}

export { App, ItemCreator };
