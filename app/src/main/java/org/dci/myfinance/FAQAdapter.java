package org.dci.myfinance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private List<SupportActivity.FAQ> faqList;
    private Context context;

    public FAQAdapter(List<SupportActivity.FAQ> faqList, Context context) {
        this.faqList = faqList;
        this.context = context;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_recycler_view_item, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        SupportActivity.FAQ faq = faqList.get(position);
        holder.questionText.setText(faq.getQuestion());
        holder.answerText.setText(faq.getAnswer());

        holder.answerText.setVisibility(faq.isAnswerVisible() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            boolean isVisible = faq.isAnswerVisible();
            if (isVisible) {
                Animation collapseAnimation = AnimationUtils.loadAnimation(context, R.anim.collapse);
                collapseAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.answerText.setVisibility(View.GONE);
                        faq.setAnswerVisible(false);
                        notifyItemChanged(holder.getAdapterPosition());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                holder.answerText.startAnimation(collapseAnimation);
            } else {
                holder.answerText.setVisibility(View.VISIBLE);
                Animation expandAnimation = AnimationUtils.loadAnimation(context, R.anim.expand);
                holder.answerText.startAnimation(expandAnimation);
                faq.setAnswerVisible(true);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    static class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView answerText;

        FAQViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            answerText = itemView.findViewById(R.id.answer_text);
        }
    }
}
