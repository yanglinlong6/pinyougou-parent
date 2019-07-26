var app = new Vue({
    el:"#app",
    data:{
        address:{alias:""},
        addresssList:[]
    },
    methods:{
        addAddress:function () {
            axios.post("/address/addAddress.shtml",this.address).then(function (response) {
                alert(response.data.message)
                app.findAllAddress()
            })
        },
        findAllAddress:function () {
            axios.get("/address/findAll.shtml").then(function (response) {
                app.addresssList = response.data
            })
        },
        deleteAddress:function (id) {
            axios.get("/address/deleteAddress.shtml?id="+id).then(function (response) {
                alert(response.data.message)
                app.findAllAddress()
            })
        },
        findByAddressId:function (id) {
            axios.get("/address/findById.shtml?id="+id).then(function (response) {
               app.address = response.data;
            })
        }
    },

    created:function () {
        this.findAllAddress();
    }
});