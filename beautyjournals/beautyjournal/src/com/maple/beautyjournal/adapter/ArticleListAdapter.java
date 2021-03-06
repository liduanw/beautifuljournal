package com.maple.beautyjournal.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.entitiy.Article;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.*;

/**
 * Created by 子星 on 2014/4/30.
 */
public class ArticleListAdapter extends BaseAdapter {

    private List<List<Article>> mArticles = new ArrayList<List<Article>>();
    Article article = null ;
    private Context mContext;
    LayoutInflater layoutInflater = null;
    List<Article> articleList = null ;
    boolean hasPicture = false ;
    int firstPictureNo = -1 ;
    private View.OnClickListener mOnArticleItemClickListener;
    private static final long MILLI_SECONDS_IN_HOUR = 3600 * 1000 + 1 ;

    public ArticleListAdapter(Context context, List<Article> articleList) {
        this.layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.mArticles = articles;
        this.articleList = articleList ;
        //articleList = mArticles.get(0);
        this.mContext = context ;

        for (int i = 0 ; i < articleList.size() ; i++){
            article = articleList.get(i) ;
            if (article.hasPic()){
                hasPicture  = true ;
                firstPictureNo = i ;
                break;
            }
        }

    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.mOnArticleItemClickListener = onClickListener;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (i == 0){
            if (hasPicture) {
                view = layoutInflater.inflate(R.layout.article_list_view_image_item, null);
                DisplayImageOptions options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build() ;

                ImageView articleImageView = (ImageView)view.findViewById(R.id.article_image_view) ;
                TextView articleTextView = (TextView) view.findViewById(R.id.article_title);
                article = articleList.get(firstPictureNo) ;
                articleTextView.setText(article.title);
                articleImageView.setTag(article);
                articleImageView.setOnClickListener(mOnArticleItemClickListener);
                //ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this.mContext)
                ImageLoader.getInstance().displayImage(article.pic, articleImageView , options);
                articleImageView.setTag(article);
            }
        }
        else {
            int currentItem = -1 ;
            TextView articleTitleTextView = null;
            TextView articleTimeTextView = null;
            int picRow = -1 ;
            picRow = (firstPictureNo *2)/3+1;
            if (i<picRow){
                currentItem = i -1 + (i-1)/2 ;
                //article = articleList.get(i-1) ;
            }
            else {
                currentItem = i + (i-1)/2 ;
            }
            article = articleList.get(currentItem);

            if (i%2==0 && currentItem < articleList.size()-1){
                view = layoutInflater.inflate(R.layout.article_list_view_text_small_item , null) ;
                RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.article_item_left) ;
                articleTitleTextView = (TextView)view.findViewById(R.id.article_title_left) ;
                articleTimeTextView = (TextView)view.findViewById(R.id.article_time_left) ;
                if (article.title.length() > 12){
                    articleTitleTextView.setTextSize(16);
                }
                articleTitleTextView.setText(article.title);
                String relativeTime = getRelativeTimeString(article.releaseTime);
                articleTimeTextView.setText(relativeTime);
                relativeLayout.setTag(article);
                relativeLayout.setOnClickListener(mOnArticleItemClickListener);
                if (System.currentTimeMillis() - article.releaseTime * 1000 < MILLI_SECONDS_IN_HOUR) {
                    //in the same hour, show "new" indicator
                    articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources()
                            .getDrawable(R.drawable.new_article_indicator), null);
                } else {
                    articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                if (currentItem +1 == firstPictureNo ){
                    currentItem++ ;
                }
                if (currentItem == articleList.size() - 1){
                    return view ;
                }

                article = articleList.get(currentItem+1) ;

                relativeLayout = (RelativeLayout)view.findViewById(R.id.article_item_right) ;
                articleTitleTextView = (TextView)view.findViewById(R.id.article_title_right) ;
                articleTimeTextView = (TextView)view.findViewById(R.id.article_time_right) ;
                if (article.title.length() > 12){
                    articleTitleTextView.setTextSize(16);
                }
                articleTitleTextView.setText(article.title);
                relativeTime = getRelativeTimeString(article.releaseTime);
                articleTimeTextView.setText(relativeTime);
                relativeLayout.setTag(article);
                relativeLayout.setOnClickListener(mOnArticleItemClickListener);
                if (System.currentTimeMillis() - article.releaseTime * 1000 < MILLI_SECONDS_IN_HOUR) {
                    //in the same hour, show "new" indicator
                    articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources()
                            .getDrawable(R.drawable.new_article_indicator), null);
                } else {
                    articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            else {
                view = layoutInflater.inflate(R.layout.article_list_view_text_item, null);
                articleTitleTextView = (TextView) view.findViewById(R.id.article_title);
                articleTimeTextView = (TextView) view.findViewById(R.id.article_time);

                articleTitleTextView.setText(article.title);
                String relativeTime = getRelativeTimeString(article.releaseTime);
                articleTimeTextView.setText(relativeTime);
                view.setTag(article);
                view.setOnClickListener(mOnArticleItemClickListener);
                if (System.currentTimeMillis() - article.releaseTime * 1000 < MILLI_SECONDS_IN_HOUR) {
                    //in the same hour, show "new" indicator
                    articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources()
                            .getDrawable(R.drawable.new_article_indicator), null);
                } else {
                    articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                //articleTimeTextView.setText(article);
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return (articleList.size() *2/3)+1;
    }

    @Override
    public Object getItem(int i) {
        return mArticles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private String getRelativeTimeString(long time) {
        time = time*1000;
        long current = System.currentTimeMillis();
        if (current - time < MILLI_SECONDS_IN_HOUR) {
            return DateUtils.getRelativeTimeSpanString(time, System
                    .currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } else if (DateUtils.isToday(time)) {
            return getTodayRelativeTime(time);
        } else if (isYear(time)) {
            new DateFormat();
            return DateFormat.format("MM/dd", new Date(time)).toString();
        } else {
            new DateFormat();
            return DateFormat.format("yy/MM/dd", new Date(time)).toString();
        }
    }

    private String getTodayRelativeTime(long time) {
        int hour = (int) ((System.currentTimeMillis() - time) / (MILLI_SECONDS_IN_HOUR));
        return String.format(mContext.getString(R.string.hours_ago), hour);
    }

    private boolean isYear(long time) {
        GregorianCalendar calendar = new GregorianCalendar();
        Date date = new Date(time);
        GregorianCalendar calendar1 = new GregorianCalendar();
        calendar1.setTime(date);
        return calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR);
    }
}
