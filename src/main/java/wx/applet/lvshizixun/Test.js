function a(){
    var c="2020-11-22T16:00:00.000+00:00"
    var date = new Date();
    console.log(date.getFullYear()-parseInt(c.slice(0,4)))
}
a()