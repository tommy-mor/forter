/* eslint-disable */
import "./styles.css";
import React, { useState, Fragment } from "react";
import "./const.js";

export default function App() {
  return (
    <div className="App">
      <h1>nate waz here</h1>
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
  title: true,
  url: null,
  paragraph: false,
  img: false
};

const ExampleInputs = {
  title: <input type="text" placeholder="rick" />,
  url: (
    <input
      type="url"
      placeholder="https://www.youtube.com/watch?v=dQw4w9WgXcQ"
    />
  ),
  paragraph: <textarea placeholder="best song ever" />
};

function TagCreator() {
  const [format, setFormat] = useState(Object.assign({}, Formats));
  const [urlFormat, setUrlFormat] = useState(Object.assign({}, UrlFormats));

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
    } else {
      setUrlFormat({ ...urlFormat, [type]: !urlFormat[type] });
    }
  };

  // this is weird IDK what else to do
  const inputListFromFormat = (format) => {
    return Object.keys(format).reduce(
      (list, field) => (format[field] ? [...list, field] : list),
      // start object, makes sure returns list
      []
    );
  };

  return (
    <div className="tag-creator">
      <ExampleItemCreator props={{ inputList: inputListFromFormat(format) }} />
      <form action="/action_page.php">
        Title <br />
        <div onChange={() => handleChange("url")}>
          Url?:
          <input type="radio" name="url" value="Yes" checked={format["url"]} />
          <label for="Yes">Yes</label>
          <input type="radio" name="url" value="No" checked={!format["url"]} />
          <label for="No">No</label>
        </div>
        {format["url"]
          ? Object.keys(urlFormat).map((type) => (
              <Fragment>
                <input
                  type="checkbox"
                  id={type}
                  name={type}
                  value={type}
                  key={type}
                  checked={urlFormat[type]}
                  onChange={() => handleUrlChange(type)}
                />
                <label for="vehicle1">{type}</label>
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
          <label for="Yes">Yes</label>
          <input
            type="radio"
            name="paragraph"
            value="No"
            checked={!format["paragraph"]}
          />
          <label for="No">No</label>
        </div>
      </form>
    </div>
  );
}

function ItemCreator(props) {}

function ExampleItemCreator({ props }) {
  // console.log(props);
  return (
    <form>
      {props["inputList"].map((inputName) => (
        <Fragment>
          {ExampleInputs[inputName]}
          <br />
        </Fragment>
      ))}
    </form>
  );
}
