/* eslint-disable */
import "./styles.css";
import React, { useState, useEffect, Fragment } from "react";
import "./const.js";

API_URL = "dummy";

async function postData(url = "", data = {}) {
  // Default options are marked with *
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
  });
  return response.json(); // parses JSON response into native JavaScript objects
}

export default function App() {
  return (
    <div className="App">
      <h1>nate was here</h1>
      <h2>Start editing to see some magic happen!</h2>
      <TagCreator />
    </div>
  );
}

const UrlFormats = {
  "any website": true,
  "image link": true,
  youtube: true,
  "youtube with timestamp": true,
  spotify: true,
  twitter: true
};


const Formats = {
  name: true,
  url: null,
  paragraph: false,
  img: false
};

// items can be "owner", "anybody", "users", or a user ID

const Permissions = {
  view_tag: [],
  add_items: [],
  vote: []
};

dummyUserNames = ["tommy", "nate", "blobbed", "bobathan"];

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

function TagCreator() {
  const [format, setFormat] = useState(Object.assign({}, Formats));
  const [urlFormat, setUrlFormat] = useState(Object.assign({}, UrlFormats));
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [permissions, setPermissions] = useState(
    JSON.parse(JSON.stringify(Permissions))
  );

  const handleChange = (field) => {
    setFormat({ ...format, [field]: !format[field] });
  };

  const handleUrlChange = (type) => {
    if (type === "any website") {
      setUrlFormat(
        Object.keys(urlFormat).reduce(
          (prev, type) => ({ ...prev, [type]: !urlFormat["any website"] }),
          {}
        ),
        () => console.log(urlFormat)
      );
      console.log(urlFormat);
    } else if (!urlFormat["any website"]) {
      setUrlFormat({ ...urlFormat, [type]: !urlFormat[type] });
    }
  };

  const handleSubmit = () => {
    data = {
      title: title,
      description: description,
      permissions: permissions,
      format: { ...format, url: format.url ? UrlFormats : false }
    };
    console.log(data);
    confirm("Create this tag?") ? postData(API_URL, data) : null;
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

      <PermissionsPicker
        permissions={permissions}
        setPermissions={setPermissions}
        userNames={dummyUserNames}
      />
      <div className="tag-creator">
        <ExampleItemCreator inputList={inputListFromFormat(format)} />
        <form action="/action_page.php">
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
            ? Object.keys(urlFormat).map((type) => (
                <Fragment key={type}>
                  <input
                    type="checkbox"
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
      <input type="submit" onClick={handleSubmit} />
    </div>
  );
}

function ItemCreator(props) {}

function ExampleItemCreator(props) {
  // console.log(props);
  return (
    <form>
      What the Add Item form Will Look Like
      <br />
      {props["inputList"].map((inputName) => (
        <Fragment key={inputName}>
          {ExampleInputs[inputName]}
          <br />
        </Fragment>
      ))}
    </form>
  );
}

function PermissionsPicker({ permissions, setPermissions, userNames }) {
  const [listOfUsers, setListOfUsers] = useState([]);
  const [formState, setFormState] = useState({});

  //TODO: initialize form state from server
  // const formStateFromPermissions = {
  // }

  // refactor or no?
  const handleChange = ({ target }) => {
    anybody = target.dataset.permission + "-anybody";
    users = target.dataset.permission + "-users";
    list = target.dataset.permission + "-list";
    if (target.dataset.usertype === "anybody") {
      setFormState({
        ...formState,
        [anybody]: target.checked,
        [users]: target.checked,
        [list]: target.checked
      });
    } else if (target.dataset.usertype === "users") {
      setFormState({
        ...formState,
        [users]: target.checked || formState[anybody],
        [list]: target.checked || formState[anybody]
      });
    } else if (target.dataset.usertype === "list") {
      setFormState({
        ...formState,
        [list]: target.checked || formState[anybody] || formState[users]
      });
    }
  };

  const handleUserName = ({ target }) => {
    listOfUsers.includes(target.value)
      ? setListOfUsers(listOfUsers.filter((user) => user !== target.value))
      : setListOfUsers(listOfUsers.concat([target.value]));
  };
  // update permissions with form updates
  useEffect(() => {
    const newPermissions = {};
    Object.keys(formState).forEach((formItem) => {
      if (formState[formItem]) {
        [permission, usertype] = formItem.split("-");
        if (usertype === "list") {
          !newPermissions[permission]
            ? (newPermissions[permission] = listOfUsers)
            : newPermissions[permission].push.apply(
                newPermissions[permission],
                listOfUsers
              );
        } else {
          !newPermissions[permission]
            ? (newPermissions[permission] = [usertype])
            : newPermissions[permission].push(usertype);
        }
      }
    });
    setPermissions(newPermissions);
  }, [formState, listOfUsers]);

  const isChecked = (usertype, permission) => {
    return formState[permission + "-" + usertype];
  };
  return (
    <form>
      Anyone on the Internet:
      <input
        type="checkbox"
        data-usertype="anybody"
        data-permission="view_tag"
        checked={isChecked(["anybody"], "view_tag")}
        onChange={handleChange}
      />
      <label>view tag</label>
      <br />
      <br />
      Any Sorter User:
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
