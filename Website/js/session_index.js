firebase.auth().onAuthStateChanged(firebaseUser =>{
  if(firebaseUser)
  {
    window.location = 'demo.html';
  }
  else {
    //window.location = 'index.html';
  }
});
