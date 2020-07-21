var currentMineCraftType = 0;
var canBeDeleted = false;
var goMineCraftFlag = false;//是否是点击去我的世界
$(function () {

})

function switchGame(type) { //type  1: 去普通模式 0：去minecraft
    if (true) {
        removeAllShapes();
    } else {
        return
    }
    // var switchGame = $("#switchGame:checked").length;
    if (type == 1) {
        currentBuildType = 0;//0: 普通模式 1：minecraft
        $(".mine_craft_active").removeClass("mine_craft_active");
        $(".obj_control_wrapper_minecraft").hide();
        $( ".save_ask_mineCraft,.save_name_module_bg" ).hide();
        $(".minecraft_wrapper").hide();
        $(".save_name_minecraft_module").hide();
        canBeDeleted = false;

    } else if (type == 0) {
        currentBuildType = 1;//0: 普通模式 1：minecraft
        $( ".minecraft_wrapper" ).show();
        $(".obj_control_wrapper").hide();
        $( ".save_ask_mineCraft,.save_name_module_bg" ).hide();
        $(".mine_craft_active").removeClass("mine_craft_active");
        loadhouseSTL(0)
    }
    goMineCraftFlag=false;
}

async function loadhouseSTL(thisSTL, obj) {
    if (currentBuildType == 0) {
        return
    } else {
        currentObj = '';
        stlGeoFlag = 4;//0 geo; 1 stl 2, localStl 4, minecraft
        showInput(1);
        $(".mine_craft_active").removeClass("mine_craft_active");
        $(obj).addClass("mine_craft_active");
        if (!obj) {
            $(".mine_craft_0").addClass("mine_craft_active");
        }
        currentModule = 0; //编辑模式，各种基础模型
        shootedFlag = false;
        var file;
        var loader = new THREE.STLLoader();
        switch (thisSTL) {
            case 0:
                // 正方形
                currentMineCraftType = 0;
                currentObj = new THREE.BoxBufferGeometry(SHAPE_SIZE, SHAPE_SIZE, SHAPE_SIZE);
                break;
            case 1:
                // 窗户
                currentMineCraftType = 1;
                file = '../models/stl/ascii/3dPrinting/window.stl';
                await loader.load( file, function ( geometry ) {
                    currentObj = geometry;
                } );
                break;
            case 2:
                // 门
                currentMineCraftType = 2;
                file = '../models/stl/ascii/3dPrinting/door.stl';
                await loader.load( file, function ( geometry ) {
                    currentObj = geometry;
                } );
                break;
            default:
                // 正方形
                currentMineCraftType = 0;
                currentObj = new THREE.BoxBufferGeometry(SHAPE_SIZE, SHAPE_SIZE, SHAPE_SIZE);
                break;
        }
        changeMouseHelper(currentColorFlag);
    }

}

function onDocumentMouseDownMineCraft(event) {
    if (currentBuildType == 1) {
        event.preventDefault();

        var controlBoardWidth = $("#shapes").hasClass("shapes_close"); //left decal side width
        if (controlBoardWidth) {
            if (event.type == "touchend") {
                var touch = event.changedTouches[0];
                mouse.set((touch.clientX / window.innerWidth) * 2 - 1, -(touch.clientY / window.innerHeight) * 2 + 1);
            } else {
                mouse.set((event.clientX / window.innerWidth) * 2 - 1, -(event.clientY / window.innerHeight) * 2 + 1);
            }
        } else {
            if (event.type == "touchend") {
                var touch = event.changedTouches[0];
                mouse.set((touch.clientX / (window.innerWidth - 100)) * 2 - 1, -(touch.clientY / window.innerHeight) * 2 + 1);
            } else {
                mouse.set((event.clientX / (window.innerWidth - 100)) * 2 - 1, -(event.clientY / window.innerHeight) * 2 + 1);
            }
        }
        raycaster.setFromCamera(mouse, camera);

        var intersects = raycaster.intersectObjects(objects);

        if (intersects.length > 0) {

            var intersect = intersects[0];

            // delete cube

            if (canBeDeleted) {

                if (intersect.object !== plane) {

                    scene.remove(intersect.object);

                    objects.splice(objects.indexOf(intersect.object), 1);

                }

                // create cube

            } else {
                var voxelMaterial = currentObjMaterial.clone();
                var voxel = new THREE.Mesh(currentObj, voxelMaterial);
                voxel.position.copy(intersect.point).add(intersect.face.normal);
                voxel.position.divideScalar(SHAPE_SIZE).floor().multiplyScalar(SHAPE_SIZE).addScalar(SHAPE_SIZE / 2);
                voxel.name = "shapes";
                if (currentMineCraftType == 1) {
                    // voxel.rotation.set(0, 0, -Math.PI / 4);
                }
                if (voxel.position.x>45 || voxel.position.x<-45 || voxel.position.z>45 || voxel.position.z<-45 || voxel.position.y<5 || voxel.position.y>95) {
                    return
                }
                scene.add(voxel);
                objects.push(voxel);
                shapesObj.push(voxel); //全删除使用
                // transformControl.object = voxel;
                // focusedTransformObj = transformControl.object;
                createObjForOperation(voxel, 'add');
                $( ".save_stl" ).removeClass( "noActive_save" );//
                $( ".undo_control" ).removeClass( "noActive_control" );//
                $(".obj_control_wrapper_minecraft").show();
            }
            render();
        }

    }
}

function deletedModule() {
    $(".active_control").removeClass("active_control")
    if (canBeDeleted) {
        canBeDeleted = false;
    } else {
        canBeDeleted = true;
        $(".obj_control_wrapper_minecraft .delete_control").addClass("active_control")
    }
}

function swichToNormalModule() {
    if (objects.length > 1) {
        if (saveFlag) {
            switchGame(1);
        } else {
            $( ".save_ask_mineCraft .mine_craft_title").text("正在离开我的世界模式");
            $( ".save_ask_mineCraft .btn_no").attr("onclick","switchGame(1)");
            $( ".save_ask_mineCraft .btn_yes").attr("onclick","saveCraftModuleShow(0)");
            $( ".save_ask_mineCraft,.save_name_module_bg" ).show();
        }
    }
    else{
        switchGame(1);
    }
}
function saveCraftModuleShow( type ) {
    if (objects.length > 1) {
        if (type == 0) {
            $( "#save_minecraft_name" ).val( getTimeStr() );
            $( ".save_ask_mineCraft .mine_craft_title").text("正在进入我的世界模式");
            $( ".save_name_minecraft_module,.save_name_module_bg" ).show();
            $( ".save_ask_mineCraft,.save_name_module_bg" ).hide();
        } else {
            $( ".save_name_minecraft_module,.save_name_module_bg" ).hide();
        }
    } else {
        $( ".save_name_minecraft_module,.save_name_module_bg" ).hide();
    }
}

function swichToMineCraftModule() {
    hideMineCraftNote();
    if (objects.length > 1) {
        if (saveFlag) {
            switchGame(0);
        } else {
            goMineCraftFlag=true;
            saveModuleShow( 0 );
        }
    }
    else{
        switchGame(0);
    }
}
function hideMineCraftNote(){
    $( ".save_ask_mineCraft,.save_name_module_bg" ).hide();
}



// 导出相关
function exportMineCraftMoudle( type ) { //type 0: ASCII 1: GLTF
    if (objects.length > 1) {
        scene.remove( transformControl );
        scene.remove( mouseHelper );
        clearCache( gridHelper );
        scene.remove( gridHelper );
        clearCache( gradGroundMesh );
        scene.remove( gradGroundMesh );
        clearCache( gradGroundMesh1 );
        scene.remove( gradGroundMesh1 );
        clearCache( plane );
        scene.remove( plane );
        outlinePass.selectedObjects = [];
        camera.position.set( 83, 71, 124); //45°
        camera.lookAt( 0, 0, 0 );
        directionalLight.position.set( -10, 1, -20 ).normalize();
        //threejs Y-up, 别的事Z-up,所以到处之前要旋转
        scene.rotation.set( Math.PI / 2, 0, 0 );
        scene.updateMatrixWorld();
        //end
        animate();
        var nameStr = $( "#save_minecraft_name" ).val();
        var successFlag;
        if (nameStr) {
            saveFlag = true;
            if (type === 0) {
                exporter = new THREE.STLExporter(); //导出工具  exporter tool
                var result = exporter.parse( scene );
                var date = Date.parse( new Date() );
                // saveString( result, nameStr + '.stl' );
                saveAsImage(nameStr,result );
                // successFlag = true;
            } else {
                var input = scene;
                var gltfExporter = new THREE.GLTFExporter();
                var options = {
                    trs: false,
                    onlyVisible: true,
                    truncateDrawRange: true,
                    binary: false,
                    forcePowerOfTwoTextures: false,
                    maxTextureSize: 4096
                };
                gltfExporter.parse( input, function ( result ) {
                    var output = JSON.stringify( result, null, 2 );
                    var date = Date.parse( new Date() );
                    saveString( output, nameStr + '.gltf' );
                }, options );
            }
        }


        if (! mobile) {
            scene.add( mouseHelper );
        }
        scene.add( transformControl );
        transformControl.detach();//隐藏控制控件
        scene.add( gridHelper );
        scene.add( gradGroundMesh );
        scene.add( gradGroundMesh1 );
        scene.add( plane );
        //threejs Y-up, 别的事Z-up,所以到处之前要旋转
        scene.rotation.set( 0, 0, 0 );
        scene.updateMatrixWorld();
        directionalLight.position.set( 1, 0.75, 0.5 ).normalize();
        //end

        switchGame(1)
    }
}