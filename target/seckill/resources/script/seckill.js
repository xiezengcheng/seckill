var seckill = {
    //封装秒杀相关ajax的url
    URL:{
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
          return '/seckill/'+seckillId+'/exposer';
        },
        execution:function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    validatePhone: function(phone){
        console.log("进入validatePhone");
      if( phone && phone.length == 11 && !isNaN(phone)){
          console.log('has phone');
          return true;
      }else{
          console.log('no phone');
          return false;
      }

    },
    handleSeckillkill:function(seckillId,node){
        //执行秒杀逻辑
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">' +
                '开始秒杀</button>')
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //执行交互流程
            if(result && result['success']){
                 var exposer = result['data'];
                 if(exposer['exposed']){
                     //开启秒杀
                     //获取秒杀地址
                     var md5 = exposer['md5'];
                     var killUrl = seckill.URL.execution(seckillId,md5);
                     console.log("秒杀地址:"+killUrl);

                     //绑定一次点击事件  防止重复点击
                     $('#killBtn').one('click',function () {
                         //执行秒杀请求
                         $(this).addClass('disabled');
                         //发送秒杀请求
                         $.post(killUrl,{},function (result) {
                             if(result && result['success']){
                                 var killResult = result['data'];
                                 var state = killResult['state'];
                                 var stateInfo = killResult['stateInfo'];
                                 //显示秒杀结果
                                 node.html('<span class="laben label-success">'+stateInfo+'</span>');

                             }
                         });

                     });
                     node.show();
                 }else{
                     //未开启秒杀
                     var now = exposer['now'];
                     var start = exposer['start'];
                     var end = exposer['end'];
                     //服务端时间未到 重新进入计时逻辑
                     seckill.countDown(seckillId,now,start,end);
                 }
            }else{
                console.log('result:'+result);
            }

        });


    },
    countDown:function(seckillId,nowTime,startTime,endTime){
          console.log("进入了countdown");
           var seckillBox =  $('#seckill-box');
        //时间判断
        if(nowTime > endTime){
            //秒杀结束
            seckillBox.html('秒杀结束');
        }else if(nowTime < startTime){
            console.log("进入秒杀未开始");
            //秒杀还未开始
            seckillBox.countdown(startTime,function (event) {
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () {
                //时间截至时
                //获取秒杀地址，控制显示逻辑，执行秒杀按钮
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        }else{
            //秒杀开始
            seckill.handleSeckillkill(seckillId,seckillBox);
        }

    },


    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            console.log("进入init");
            //手机验证和登录 ， 计时交互
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //绑定phone
                var killPhoneModal = $('#killPhoneModal');
                console.log("接下来走killPhoneModal.modal");
                //注意是modal
                killPhoneModal.modal({
                    show: true,
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//禁止键盘关闭
                });
                console.log("接下来走click");
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone:'+inputPhone);
                    if (seckill.validatePhone(inputPhone)) {
                        //写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！！</label>').show(300);
                    }
                });
            }
            //已经登录
            //计时交互
            $.get(seckill.URL.now(),{},function (result) {
                if(result && result['success']){
                    var nowTime = result['data'];
                    console.log("接下来走countdown判断");
                    //时间判断
                    seckill.countDown(seckillId,nowTime,startTime,endTime);

                }else{
                    console.log('result:'+result);

                }

            });


        }
    }
};