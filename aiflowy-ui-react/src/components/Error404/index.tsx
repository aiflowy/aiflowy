import React from 'react';
// import error404 from "../../assets/login-image.png";
import error404 from "../../assets/404.png";


const Error404: React.FC = () => {
    return (
        <div style={{textAlign: "center"}}>
            <img src={error404} style={{maxWidth: "40%"}} alt="404 error"/>
            <h1>404</h1>
            <p>诶呀，找不到当前页面了...</p>
        </div>
    );
};

export default Error404;
