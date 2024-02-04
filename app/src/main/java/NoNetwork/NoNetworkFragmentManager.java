package NoNetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class NoNetworkFragmentManager extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment currentFragment = fragmentManager.findFragmentById(android.R.id.content);


    public void showNoNetworkFragment(final FragmentActivity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoNetworkFragment noNetworkFragment = new NoNetworkFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, noNetworkFragment)
                        .commitAllowingStateLoss();
            }
        });
    }



    public void hideNoNetworkFragment(FragmentActivity activity) {
        NoNetworkFragment noNetworkFragment = (NoNetworkFragment) activity.getSupportFragmentManager()
                .findFragmentById(android.R.id.content);

        if (noNetworkFragment != null && !noNetworkFragment.isDetached()) {
            activity.getSupportFragmentManager().beginTransaction().remove(noNetworkFragment).commitAllowingStateLoss();
        }
    }
}
