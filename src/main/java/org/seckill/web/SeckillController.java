package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillColseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")//模块  url:/模块/资源{id}/细分
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String list(Model model){
        //获取列表数据
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }


    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId,Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getSeckillById(seckillId);
        if(seckill == null){
            return "forword:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    @ResponseBody
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){

        SeckillResult<Exposer> result = null;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return  result;
    }


    @RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,@PathVariable("md5") String md5, @CookieValue(value = "killPhone",required = false) Long phone){

        if(phone == null){
            return new SeckillResult<SeckillExecution>(true,"未注册");
        }
        SeckillResult<SeckillExecution> result = null;
        try{

            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            //使用存储过程优化
//            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);

            result = new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillColseException e1){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.End);
            result = new SeckillResult<SeckillExecution>(true,execution);
        }catch (RepeatKillException e2){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            result = new SeckillResult<SeckillExecution>(true,execution);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            result =  new SeckillResult<SeckillExecution>(true,execution);
        }
        return  result;
    }


    @ResponseBody
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult(true,now.getTime());

    }



}
