/* eslint-disable */
import React, { useState, useEffect, Fragment } from "react";

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
	console.log('settings2')
	console.log(settings)

	return (
    <div className="App">
		<TagCreator initstate={settings}/>
    </div>
  );
}


function TagCreator({initstate}) {
  // const [tagState, setState] = useState(initstate)
  const [format, setFormat] = useState(Object.assign({}, initstate["format"]));
	const [urlFormat, setUrlFormat] = useState(Object.assign({}, initstate["format"]["url"]));
  const [title, setTitle] = useState(initstate["title"]);
  const [description, setDescription] = useState(initstate["description"]);
  const [permissions, setPermissions] = useState(
    initstate["perms"]["perms"]
  );
  const [listOfUsers, setListOfUsers] = useState([]);

  // const handleChange = ()

  const handleFormatChange = (field) => {
    setFormat({ ...format, [field]: !format[field] });
  };

  const handleUrlChange = (type) => {
	  setUrlFormat(
		  {...
		   Object.keys(urlFormat).reduce(
			   (prev, type) => ({ ... prev, [type]: false}), {}
		   ),
		   [type]: true}
	  );
  };

  const handleDelete = () => {
	  if(confirm('delete this tag?')) {
		  window.location.href = initstate["deleteurl"]
		  //postData(initstate["deleteurl"], tagid);
	  }
  };

  const handleSubmit = () => {
    data = {
      title: title,
      description: description,
		  permissions: {perms: permissions, users: listOfUsers},
      format: { ...format, url: format.url ? urlFormat : false }
    };
	  if(initstate["editing"]) {
		  // tagid is global set by server in <script> tag
		  data.tag_id = tagid;
	  }
      postData(initstate["submiturl"], data);
  };

  // this is weird IDK what else to do
  const inputListFromFormat = (format) => {
    // list contains format fields with value true
    return ["name", "url", "paragraph"].filter(field => format[field]);
  };

  return (
    <div>
      <label htmlFor="title">Title:</label>
      <input
        type="text"
        name="title"
        value={title}
        onChange={(event) => setTitle(event.target.value)}
      />
      <br/>
      <label htmlFor="description">description:</label>
      <input
        type="text"
        name="description"
        value={description}
        onChange={(event) => setDescription(event.target.value)}
      />
      <br/>
      <label> Permissions: </label>
      <PermissionsPicker
        permissions={permissions}
        setPermissions={setPermissions}
        userNames={usernames /* this comes from the backend in a <srcipt>var*/}
        listOfUsers={listOfUsers}
        setListOfUsers={setListOfUsers}
      />
      <FormatPicker
        inputList={inputListFromFormat(format)}
        handleFormatChange={handleFormatChange}
        handleUrlChange={handleUrlChange}
        editing={initstate["editing"]}
        format={format}
        urlFormat={urlFormat}
      />

		{!initstate["editing"] ? nil
		 : <input type="submit" value="delete this tag" onClick={handleDelete} />
	    }
		<input type="submit" value={initstate["editing"] ? "commit changes" : "create tag"} onClick={handleSubmit} />
    </div>
  );
}


function ItemCreator({inputList, isDummy}) {
	const [form, setForm] = useState({});
	const handleChange = (event, name) => setForm({...form, [name]: event.target.value})
	const value = (name) => form[name] ?? ''

	const handleSubmitItem = (event) => {
		event.preventDefault() //otherwise it refreshes page?
		console.log(event)
		frontsorter.core.add_item(form, ()=>setForm({}))
	}
  const inputElements = {
    name: (
      <input 
        className="addinput" type="text" placeholder="item title"
        value={value("name")}
        onChange={(event) => handleChange(event, "name")}
        disabled={isDummy}
      />
    ),
    url: (
      <input
        className="addinput" type="url" placeholder="https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        value={value("url")}
        onChange={(event) => handleChange(event, "url")}
        disabled={isDummy}
      />
    ),
    paragraph: (
      <textarea
        className="addinput" placeholder="best song ever"
        value={value("paragraph")}
        onChange={(event) => handleChange(event, "paragraph")}
        disabled={isDummy}
      />
    ),
  };
  return (
    <div>
		{inputList.map((inputName) => (
        <Fragment key={inputName}>
          {inputElements[inputName]}
          <br/>
        </Fragment>
      ))}
		 {isDummy ? null : <input type="submit" value="add item" onClick={handleSubmitItem} />}
    </div>
  );
}

function FormatPicker({inputList, handleFormatChange, handleUrlChange, editing, format, urlFormat}) {
  if(editing){
    return null
  }

  const ToggleButton = ({inputType}) => (
    <div onChange={() => handleFormatChange(inputType)}>
      Should items have {inputType}s ?:
      <input type="radio" name={inputType} value="Yes" checked={format[inputType]} />
      <label htmlFor="Yes">Yes</label>
      <input type="radio" name={inputType} value="No" checked={!format[inputType]} />
      <label htmlFor="No">No</label>
    </div>
  )

  const UrlFormatPicker = () => (
    <Fragment>
		{["any website",
		  "image link",
		  "twitter",
		  "youtube",
		  "youtube with timestamp",
		  "spotify"].map((type) => (
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
        <br/>
      </Fragment>
    ))}
    </Fragment>
  )

  return (
    <Fragment>
      <label> What the Add Item form Will Look Like </label>
      <div className="format-picker">
        <ItemCreator inputList={inputList} isDummy/>
        <form>
          Name <br/>
          <ToggleButton inputType="url"/>
          {format["url"] ? <UrlFormatPicker /> : null}
          <ToggleButton inputType="paragraph"/>
        </form>
      </div>
    </Fragment>
  )
}

function PermissionsPicker({ permissions, setPermissions, listOfUsers, setListOfUsers, userNames }) {
  // refactor or no?
  const handleChange = (usertype, permission, { target }) => {
    if (usertype === "anybody") {
      setPermissions({
        ...permissions,
        [permission + "__anybody"]: target.checked,
        [permission + "__users"]: target.checked,
        [permission + "__list"]: target.checked
      });
    } else if (usertype === "users") {
      setPermissions({
        ...permissions,
          [permission + "__users"]:  permissions[permission + "__anybody"] || target.checked,
        [permission + "__list"]:  permissions[permission + "__anybody"] || target.checked
      });
    } else if (usertype === "list") {
      setPermissions({
        ...permissions,
        [permission + "__list"]: permissions[permission + "__users"] || target.checked
      });
    }
  };

  const UserList = () => {
    const handleUserName = ({ target }) => {
      listOfUsers.includes(target.value)
        ? setListOfUsers(listOfUsers.filter((user) => user !== target.value))
        : setListOfUsers(listOfUsers.concat([target.value]));
    };
    return (<Fragment>
      {listOfUsers.join(", ") + ", "}
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
            {listOfUsers.includes(user) ? '*' : ''}
          </option>
        ))} 
      </select>
    </Fragment>)
  }

  const isChecked = (usertype, permission) => {
    return permissions[permission + "__" + usertype];
  };

  const PermissionsCheckbox = ({usertype, permission, disabled}) => {
    return (
      <Fragment>
        <input
          type="checkbox"
          checked={isChecked(usertype, permission)}
          onChange={event => handleChange(usertype, permission, event)}
          disabled={disabled}
        />
        <label>{permission.replace("_", " ")}</label>
      </Fragment>
    )
  }
  return (
    <form className="permissionform">
      Anyone on the Internet:
      <br/>
      <PermissionsCheckbox usertype="anybody" permission="view_tag"/>
      <br/>
      Any Sorter User:
      <br/>
      <PermissionsCheckbox usertype="users" permission="view_tag"/>
      <PermissionsCheckbox usertype="users" permission="vote"/>
      <PermissionsCheckbox usertype="users" permission="add_items"/>
      <br/>
      Any User in this list: 
      <UserList />
      <br/>
      <PermissionsCheckbox usertype="list" permission="view_tag" disabled={!listOfUsers.length}/>
      <PermissionsCheckbox usertype="list" permission="vote" disabled={!listOfUsers.length}/>
      <PermissionsCheckbox usertype="list" permission="add_items" disabled={!listOfUsers.length}/>
    </form>
  );
}

export { App, ItemCreator };
