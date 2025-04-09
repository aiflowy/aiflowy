import React from 'react';
import error403 from "../../assets/403.png";


const Error403: React.FC = () => {
    return (
        <div style={{textAlign: "center"}}>
            <img src={error403} style={{maxWidth: "40%"}} alt="403error"/>
            <h1>403</h1>
            <p>诶呀，当前页面禁止访问了...</p>
        </div>
    );
};

export default Error403;
