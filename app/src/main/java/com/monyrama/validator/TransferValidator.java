package com.monyrama.validator;

import android.content.Context;

import com.monyrama.R;
import com.monyrama.model.Transfer;
import com.monyrama.util.StringUtil;

/**
 * Created by petroverheles on 7/26/16.
 */
public class TransferValidator extends AbstractValidator {

    private final String SUM_REG_EXP = "[0-9]{0,63}(\\.[0-9]{1,2}){0,1}";

    private Transfer transfer;

    private Context context;

    public TransferValidator(Transfer transfer, Context context) {
        this.transfer = transfer;
        this.context = context;
    }

    @Override
    public boolean validate() {
        if(StringUtil.emptyString(transfer.getFromRawSum())) {
            setMessage(context.getString(R.string.empty_sum));
            return false;
        }

        if(!transfer.getFromRawSum().matches(SUM_REG_EXP)) {
            setMessage(context.getString(R.string.invalid_sum));
            return false;
        }

        if(StringUtil.emptyString(transfer.getToRawSum())) {
            setMessage(context.getString(R.string.empty_sum));
            return false;
        }

        if(!transfer.getToRawSum().matches(SUM_REG_EXP)) {
            setMessage(context.getString(R.string.invalid_sum));
            return false;
        }

        if(transfer.getFromAccount().getAccountId().equals(transfer.getToAccount().getAccountId())) {
            setMessage(context.getString(R.string.cant_transfer_to_same_account));
            return false;
        }

        return true;
    }
}
