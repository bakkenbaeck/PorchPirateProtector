package no.bakkenbaeck.porchpirateprotector.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideSoftKeyboard() {
    context?.let { validContext ->
        val imm = validContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.let { manager ->
            if (manager.isAcceptingText) {
                activity?.window?.currentFocus?.windowToken?.let { token ->
                    manager.hideSoftInputFromWindow(token, 0)
                } // else, we couldn't get the token, so we can't dismiss
            } // else, we don't need to dismiss.
        } // else, we couldn't get the manager, so we can't dismiss
    } // else, we coudln't get the context, so we can't dismiss.
}