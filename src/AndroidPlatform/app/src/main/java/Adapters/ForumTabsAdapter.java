package adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import fragments.ChatFragment;
import fragments.ContactsFragment;
import fragments.GroupChatFragment;
import fragments.RequestsFragment;

/**
 *
 * Type: Adapter
 * Adapter for holding the ForumActivity ViewPager to switch between fragments (Chats, Groups, Contacts, Requests)
 *
 **/
public class ForumTabsAdapter extends FragmentPagerAdapter
{

    /**
     *
     * Type: Constructor
     * Takes in a FragmentManagerAdapter which is a ViewPager object
     *
     **/
    @SuppressWarnings("deprecation")
    public ForumTabsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    /**
     *
     * Type: Function
     * Takes integer position of viewpager and return fragment object which is handled by a switch statement
     *
     **/
    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:
                return new ChatFragment();
            case 1:
                return new GroupChatFragment();
            case 2:
                return new ContactsFragment();
            case 3:
                return new RequestsFragment();
            default:
                //noinspection ConstantConditions
                return null;
        }

    }

    /**
     *
     * Type: Function
     * Return count of FragmentViewPager object
     *
     **/
    @Override
    public int getCount()
    {
        return 4;
    }

    /**
     *
     * Type: Function
     * Retrieves title of ViewPager fragment item. Used as an aid for screen readers which can help people with disabilities
     *
     **/
    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch(position)
        {
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Requests";
            default:
                return null;
        }
    }
}
