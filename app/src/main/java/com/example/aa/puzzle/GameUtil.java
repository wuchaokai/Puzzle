package com.example.aa.puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aa on 2017/9/23.
 */

public class GameUtil {
    public static List<ItemBean>mItemBeans=new ArrayList<>();
    public static ItemBean mBlankItemBean=new ItemBean();
    public static void getPuzzleGenerator(){
        int index=0;

        for (int i=0;i<mItemBeans.size();i++) {
            index=(int)(Math.random()*Puzzle.Type*Puzzle.Type);
            swapItmes(mItemBeans.get(index), GameUtil.mBlankItemBean);
        }
        List<Integer>data=new ArrayList<>();
        for (int i=0;i<mItemBeans.size();i++)
            data.add(mItemBeans.get(i).getmBitmapId());
        if (canSolve(data)&&!GameUtil.isSuccess())
            return;
        else getPuzzleGenerator();
    }
    /*
    判断拼图是否有解
     */
    private static boolean canSolve(List<Integer> data) {
        int blankId=GameUtil.mBlankItemBean.getmItemId();
        if (data.size()%2==1){
            return getInversions(data)%2==0;
        }
        else {
            if (((blankId-1)/Puzzle.Type)%2==1)
                return getInversions(data)%2==0;
            else return getInversions(data)%2==1;
        }
    }
    /*
    求该拼图序列的倒置和
     */
    private static int getInversions(List<Integer> data) {
        int inversion=0;
        int inversionCount=0;
        for (int i=0;i<data.size();i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index)
                inversionCount = inversionCount + 1;
            }
            inversion=inversion+inversionCount;
            inversionCount=0;
        }
        return inversion;
    }

    public static void swapItmes(ItemBean from, ItemBean blank) {
        ItemBean itemBeam=new ItemBean();
        itemBeam.setmBitmapId(from.getmBitmapId());
        from.setmBitmapId(blank.getmBitmapId());
        blank.setmBitmapId(itemBeam.getmBitmapId());
        itemBeam.setmBitmap(from.getmBitmap());
        from.setmBitmap(blank.getmBitmap());
        blank.setmBitmap(itemBeam.getmBitmap());
        GameUtil.mBlankItemBean=from;
    }
    public static boolean isMovable(int position){
        int type=Puzzle.Type;
        int blank=GameUtil.mBlankItemBean.getmItemId()-1;
        if (Math.abs(blank-position)==type)
            return true;
        if ((blank/type==position/type)&&Math.abs(blank-position)==1)
            return true;
        return false;
    }
    public static boolean isSuccess(){
        for (ItemBean itemBean:GameUtil.mItemBeans){
            if (itemBean.getmBitmapId()!=0&&itemBean.getmBitmapId()==itemBean.getmItemId())
                continue;
            else if (itemBean.getmBitmapId()==0&&itemBean.getmItemId()==Puzzle.Type*Puzzle.Type)
                continue;
            else return false;
        }
        return true;
    }
}
