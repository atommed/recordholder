import React from 'react'

function isAscii(str){
    for(let i = 0; i < str.length; i++)
        if(str.charCodeAt(i) > 127) return false;
    return true;
}

class AuthForm extends React.Component{
    constructor(){
        super();
        this.state = {login:"",password:""};
    }

    onSignUp = ()=>this.props.handleSignUp(this.state);
    onSignIn = ()=>this.props.handleSignIn(this.state);
    loginValid = ()=>{
        const login = this.state.login;
        return login.length > 0 && isAscii(login);
    };
    passwordValid = ()=>this.state.password.length >= 6;
    loginInputClass = ()=>{
        if(this.state.login.length == 0) return "";
        return this.loginValid() ? "valid" : "invalid";
    };
    passwordInputClass = ()=>{
        if(this.state.password.length == 0) return "";
        return this.passwordValid() ? "valid" : "invalid";
    };
    handleSubmit = (e)=>e.preventDefault();
    updateLogin = (e)=>this.setState({...this.state, login: e.target.value});
    updatePassword = (e)=>this.setState({...this.state, password: e.target.value});
    isButtonsDisabled = ()=>!(this.loginValid() && this.passwordValid());

    render() {
        return (
            <div className="row">
                <form onSubmit={this.handleSubmit}>
                    <div className="row">
                        <h4>You shall sign in or register before using this site</h4>
                    </div>
                    <div className="row">
                        <div className="input-field col s6">
                            <input onChange={this.updateLogin} name="login" id="login" type="text" className={this.loginInputClass()} />
                            <label htmlFor="login">Username</label>
                        </div>
                        <div className="input-field col s6">
                            <input onChange={this.updatePassword} name="password" id="password" type="password" className={this.passwordInputClass()} />
                            <label htmlFor="password">Password</label>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <button
                                disabled={this.isButtonsDisabled()}
                                onClick={this.onSignIn}
                                className="colored waves-effect btn">
                                <i style={{"outline" : "none","verticalAlign":"middle"}} className="material-icons">perm_identity</i>
                                Sign In
                            </button>
                        </div>
                        <div className="col">
                            <button
                                disabled={this.isButtonsDisabled()}
                                onClick={this.onSignUp}
                                className="colored waves-effect btn">
                                <i style={{"outline" : "none","verticalAlign":"middle"}} className="material-icons">person_add</i>
                                Register
                            </button>
                        </div>
                    </div>
                    { this.props.status &&(
                        <div className="row"><h5>{this.props.status}</h5></div>
                    )
                    }
                </form>
            </div>
        )
    }
}
AuthForm.propTypes = {
    status: React.PropTypes.string,
    handleSignIn: React.PropTypes.func.isRequired,
    handleSignUp: React.PropTypes.func.isRequired
};

export default AuthForm;